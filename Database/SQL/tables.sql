--drop table if exists apiusers;
--create table apiusers (
--	id serial primary key,
--	password text not null
--);

drop table if exists appusers;
create table appusers (
	id serial primary key,
    password text not null,
    cellphone text not null unique check (cellphone ~ '+[0-9]{2}[0-9]{10}'),
    email varchar(320) not null,
    firstname text not null,
    lastname text not null,
    dob date not null
);
drop table if exists clients;
create table clients (
	id int primary key references appusers(id)
);
drop table if exists providers;
create table providers (
	id int primary key references appusers(id),
	isverified boolean not null default false,
	maxstrikes int not null,-- check (maxstrikes > 0),
	companyname text
);
drop table if exists blacklist;
create table blacklist (
	user_ int references appusers(id),
	provider int references providers(id),

	primary key(user_, provider)
);
drop table if exists strikes;
create table strikes (
	user_ int references appusers(id),
	provider int references providers(id),
	count int not null default 1 check(count > 0),
	
	primary key(user_, provider)
);
drop table if exists establishments;
create table establishments (
    id serial primary key,
    owner int references appusers(id),
    address text not null,
    name text not null,

	-- potrebbero essere due campetti da basket (nomi diversi)
	-- ma con lo stesso indirizzo in quanto attaccati
	unique(address, name)
);
drop table if exists ratings;
create table ratings (
	user_ int references appusers(id),
	establishment int references establishments(id),
	rating int not null check (rating between 1 and 5), --float per il mezzo punto non va bene
	comment text
);
drop table if exists slots;
create table slots (
	id serial primary key,
	date date not null,

	-- l'utente può lasciare le prenotazioni aperte a chiunque voglia, altrimenti può bloccarla con una password
	-- che potrà poi condividere con chi vuole
	password text,
	owner int not null references providers(id)
);
drop table if exists reservations;
create table reservations (
	slot int references slots(id),
	user_ int references appusers(id),

	primary key(slot, user_)
);

-- non possodrop table if exists slotblueprints;no essere allocate da sole, ma sono sempre allocate con la relativa periodicslotblueprint o manualslotblueprint
create table slotblueprints (
	id serial primary key,
	establishment int not null references establishments(id),
	weekdays bit(7) not null,
	reservationlimit int check(reservationlimit is null or reservationlimit > 0),
	fromdate date not null default NOW(), -- data e ora dopo la quale gli slot vengono programmati
	todate date 	-- data e ora dopo la quale gli slot non vengono più programmati
					-- se null sono programmati indefinitamente
);

-- serve per programmare slot periodici
-- al momento della prenotazione viene spawnato un periodicslot che punta alla sua blueprint
-- solo gli drop table if exists periodicslotblueprints;owner possono inserire le blueprints
create table periodicslotblueprints (
	id serial primary key references slotblueprints(id),
	fromtime time not null,
	totime time not null check(totime > fromtime)
);
drop table if exists manualslotblueprints;
create table manualslotblueprints (
	id serial primary key references slotblueprints(id),
	opentime time not null,
	closetime time not null,
	maxduration interval not null check(maxduration > '1 SECOND'::INTERVAL)
);

-- l'unica idrop table if exists periodicslots;nformazione che contraddistingue i periodicslots che fanno riferimento alla stessa blueprint è la data
create table periodicslots (
	id int primary key references slots(id),
	blueprint int not null references periodicslotblueprints(id)
);

-- il proprietario dello stabilimento potrebbe voler lasciare un certo grado di libertà agli utenti per quanto riguarda orari e date
-- nel momento in cui l'utente decide di occupare uno spazio per cui sono abilitati gli slot manuali, vengono creati al contempo
-- uno slot con gli orari desiderati e una prenotazione relativa a quello slot
-- in base adrop table if exists manualslots;lla reservation limit potranno prenotarsi altri utenti a quello slot
create table manualslots (
	id int primary key references slots(id),
	blueprint int not null references manualslotblueprints(id),
	fromtime time not null,
	totime time not null check(totime > fromtime)
);
