drop table if exists users cascade;
create table users (
	id serial primary key,
	name text not null unique,
	password_digest text not null
);

drop table if exists app_users cascade;
create table app_users (
	id serial primary key,
    password_digest text not null,
    cellphone char(12) not null unique,
    email varchar(320),
    firstname text not null,
    lastname text not null,
    dob date not null
);

drop table if exists clients cascade;
create table clients (
	id serial primary key,
	app_user_id int not null references app_users(id)
		on update cascade
		on delete cascade,
	lat float,
	lng float,

	check((lat is null and lng is null) or (lat is not null and lng is not null))
);

drop table if exists providers cascade;
create table providers (
	id serial primary key,
	app_user_id int not null references app_users(id)
		on update cascade
		on delete cascade,
	isverified boolean not null default false,
	maxstrikes int not null default 1 check(maxstrikes > 0),
	companyname text
);

drop table if exists blacklist cascade;
create table blacklist (
	id serial primary key,
	provider_id int not null references providers(id)
		on update cascade
		on delete cascade,
	usercellphone char(12),

	unique(usercellphone, provider_id)
);

drop table if exists strikes cascade;
create table strikes (
	id serial primary key,
	provider_id int not null references providers(id)
		on update cascade
		on delete cascade,
	usercellphone char(12),
	count int not null default 1 check(count > 0),
	
	unique(usercellphone, provider_id)
);

drop table if exists establishments cascade;
create table establishments (
    id serial primary key,
    provider_id int not null references providers(id)
		on update cascade
		on delete cascade,
    name text not null,
	lat float not null,
	lng float not null,

	-- potrebbero essere due campetti da basket (nomi diversi)
	-- ma con lo stesso indirizzo in quanto attaccati
	unique(address, name),
	check((lat is null and lng is null) or (lat is not null and lng is not null))
);

drop table if exists ratings cascade;
create table ratings (
	id serial primary key,
	client_id int references clients(id)
		on update cascade
		on delete set null,
	establishment_id int not null references establishments(id)
		on update cascade
		on delete cascade,
	rating int not null check (rating between 1 and 5), --float per il mezzo punto non va bene
	comment text
);

drop table if exists slots cascade;
create table slots (
	id serial primary key,
	date date not null,

	-- l'utente può lasciare le prenotazioni aperte a chiunque voglia, altrimenti può bloccarla con una password
	-- che potrà poi condividere con chi vuole
	password_digest text,
	app_user_id int not null references app_users(id)
		on update cascade
		on delete no action
);

drop table if exists reservations cascade;
create table reservations (
	id serial primary key,
	slot_id int not null references slots(id)
		on update cascade
		on delete no action,
	client_id int not null references clients(id)
		on update cascade
		on delete no action,

	unique(slot_id, client_id)
);

-- non possono essere allocate da sole, ma sono sempre allocate con la relativa periodicslotblueprint o manualslotblueprint
drop table if exists slotblueprints cascade;
create table slotblueprints (
	id serial primary key,
	establishment_id int not null references establishments(id),
	weekdays bit(7) not null,
	reservationlimit int check(reservationlimit is null or reservationlimit > 0),
	fromdate date not null default NOW(), -- data e ora dopo la quale gli slot vengono programmati
	todate date 	-- data e ora dopo la quale gli slot non vengono più programmati
					-- se null sono programmati indefinitamente
);

-- serve per programmare slot periodici
-- al momento della prenotazione viene spawnato un periodicslot che punta alla sua blueprint
-- solo gli owner possono inserire le blueprints
drop table if exists periodicslotblueprints cascade;
create table periodicslotblueprints (
	id serial primary key,
	slotblueprint_id int not null references slotblueprints(id)
		on update cascade
		on delete cascade,
	fromtime time not null,
	totime time not null check(totime > fromtime)
);

drop table if exists manualslotblueprints cascade;
create table manualslotblueprints (
	id serial primary key,
	slotblueprint_id int not null references slotblueprints(id)
		on update cascade
		on delete cascade,
	opentime time not null,
	closetime time not null check (closetime > opentime),
	maxduration interval not null check(maxduration < (closetime - opentime))
);

-- l'unica informazione che contraddistingue i periodicslots che fanno riferimento alla stessa blueprint è la data
drop table if exists periodicslots cascade;
create table periodicslots (
	id serial primary key,
	slot_id int not null references slots(id)
		on update cascade
		on delete cascade,
	periodicslotblueprint_id int not null references periodicslotblueprints(id)
		on delete cascade
		on update cascade
);

-- il proprietario dello stabilimento potrebbe voler lasciare un certo grado di libertà agli utenti per quanto riguarda orari e date
-- nel momento in cui l'utente decide di occupare uno spazio per cui sono abilitati gli slot manuali, vengono creati al contempo
-- uno slot con gli orari desiderati e una prenotazione relativa a quello slot
-- in base alla reservation limit potranno prenotarsi altri utenti a quello slot
drop table if exists manualslots cascade;
create table manualslots (
	id serial primary key,
	slot_id int not null references slots(id)
		on update cascade
		on delete cascade,
	manualslotblueprint_id int not null references manualslotblueprints(id)
		on update cascade
		on delete cascade,
	fromtime time not null,
	totime time not null check(totime > fromtime)
);
