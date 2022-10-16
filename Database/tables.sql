create table apiusers (
	id serial primary key,
    cellphone text not null unique check (cellphone ~ '+[0-9]{2}[0-9]{10}'),
	password text not null
);

create table appusers (
	id int primary key references apiusers(id),
    firstname text not null,
    lastname text not null,
    dob date not null,
    isprovider boolean not null,
    isverified boolean check (isprovider or isverified is null)
);

create table establishments (
    id serial primary key,
    address text not null,
    name text not null,
    owner int references apiusers(id),

	-- se null le prenotazioni sono di gruppo senza un particolare limite di partecipanti (campetto/sala ricreativa)
	-- se > 0 le prenotazioni sono individuali (evento a numero limitato)
	-- si riferisce agli slot manuali, gli slot periodici possono fare l'override in quanto li può decidere solo l'owner
	reservationlimit int check(limit is null or limit > 0),
	allowmanualslots boolean not null default false,

	-- potrebbero essere due campetti da basket (nomi diversi)
	-- ma con lo stesso indirizzo in quanto attaccati
	unique(address, name)
);

-- non possono essere allocate da sole, ma sono sempre allocate con la relativa periodicslotblueprint o manualslotblueprint
create table slotblueprints (
	id serial primary key,
	establishment int not null references establishments(id),
	weekdays varchar(7) not null check(weekdays ~ '[1]{0,1}[2]{0,1}[3]{0,1}[4]{0,1}[5]{0,1}[6]{0,1}[7]{0,1}'),
	reservationlimit int check(limit is null or limit > 0),
	fromdate datetime not null default NOW(), -- data e ora dopo la quale gli slot vengono programmati
	todate datetime, 	-- data e ora dopo la quale gli slot non vengono più programmati
					-- se null sono programmati indefinitamente
);

-- serve per programmare slot periodici
-- al momento della prenotazione viene spawnato un periodicslot che punta alla sua blueprint
-- solo gli owner possono inserire le blueprints
create table periodicslotblueprints (
	id serial primary key references slotblueprints(id),
	fromtime time not null,
	duration interval not null check(duration > 0)
);

create table manualslotblueprints (
	id serial primary key references slotblueprints(id),
	maxduration interval not null check(maxduration > 0),
	opentime time not null,
	closetime time not null
);

create table slots (
	id serial primary key,
	date date not null,

	-- può essere un normale utente o il proprietario dello stabilimento
	-- può gestire le prenotazioni di altri utenti al suo slot (nel rispetto della reservationlimit della blueprint)
	owner int primary key references apiusers(id),

	-- l'utente può lasciare le prenotazioni aperte a chiunque voglia, altrimenti può bloccarla con una password
	-- che potrà poi condividere con chi vuole
	password text
);

-- l'unica informazione che contraddistingue i periodicslots che fanno riferimento alla stessa blueprint è la data
create table periodicslots (
	id int primary key references slots(id),
	blueprint int not null references periodicslotblueprints(id)
);

-- il proprietario dello stabilimento potrebbe voler lasciare un certo grado di libertà agli utenti per quanto riguarda orari e date
-- nel momento in cui l'utente decide di occupare uno spazio per cui sono abilitati gli slot manuali, vengono creati al contempo
-- uno slot con gli orari desiderati e una prenotazione relativa a quello slot
-- in base alla reservation limit potranno prenotarsi altri utenti a quello slot
create table manualslots (
	id int primary key references slots(id),
	blueprint int primary key references manualslotblueprints(id),
	fromtime time not null,
	duration interval not null check(duration > 0)
);

create table reservations (
	id serial primary key,
    user text not null references apiusers(id),
    slot int not null references slots(id),
);

