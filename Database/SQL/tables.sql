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
	app_user_id bigint not null references app_users(id)
		on update cascade
		on delete cascade,

	-- users may provide the application with their preferred static location
	-- which will determine the establishments in their immediate distance
	lat float,
	lng float,

	-- they may choose however to not provide a static location and activate the
	-- geolocalization on their smartphone, which will be dynamic
	check((lat is null and lng is null) or (lat is not null and lng is not null))
);

drop table if exists providers cascade;
create table providers (
	id serial primary key,
	app_user_id bigint not null references app_users(id)
		on update cascade
		on delete cascade,

	-- verified providers are known to be trustable
	-- the verification process takes place outside the application
	isverified boolean not null default false,

	-- each provider can decide how many strikes an attendant of their establishments can get before getting banned
	-- they can however decide to ban them directly without going through the striking process
	maxstrikes int not null default 1 check(maxstrikes > 0),
	companyname text
);

drop table if exists blacklists cascade;
create table blacklists (
	id serial primary key,
	provider_id bigint not null references providers(id)
		on update cascade
		on delete cascade,

	-- not a foreign key, to prevent users to delete their account to escape blacklists
	usercellphone char(12),

	unique(usercellphone, provider_id)
);

drop table if exists strikes cascade;
create table strikes (
	id serial primary key,
	provider_id bigint not null references providers(id)
		on update cascade
		on delete cascade,

	-- not a foreign key, to prevent users to delete their account to erase their strikes history
	usercellphone char(12),
	count int not null default 1 check(count > 0),
	
	unique(usercellphone, provider_id)
);

drop table if exists establishments cascade;
create table establishments (
    id serial primary key,
    provider_id bigint not null references providers(id)
		on update cascade
		on delete cascade,
    name text not null,
	place_id text not null, -- can be useful for including additional info when hotfixing

	-- the following properties are cached, however they can be retreived at any time
	-- by submitting the place_id to the maps API
	address text not null,
	lat float not null,
	lng float not null,

	unique(address, name)
);

drop table if exists ratings cascade;
create table ratings (
	id serial primary key,

	-- may be null, we want to keep the reviews even when users delete their account
	-- needs a fix, users may still delete and recreate their account to keep cumulating feedbacks
	client_id bigint references clients(id)
		on update cascade
		on delete set null,
	establishment_id bigint not null references establishments(id)
		on update cascade
		on delete cascade,

	-- at the moment ratings are any amount of "stars" between 1 and 5 without any half points
	rating int not null check (rating between 1 and 5),
	comment text
);

drop table if exists slots cascade;
create table slots (
	id serial primary key,
	date date not null,

	-- the slot owner can leave the reservations open for anyone, or lock them with a password
	password_digest text,
	app_user_id bigint not null references app_users(id)
		on update cascade
		on delete no action
);

drop table if exists reservations cascade;
create table reservations (
	id serial primary key,
	slot_id bigint not null references slots(id)
		on update cascade
		on delete cascade,
	client_id bigint not null references clients(id)
		on update cascade
		on delete cascade,

	unique(slot_id, client_id)
);

-- they're always associated with at least one periodic_slot_blueprint or manual_slot_blueprint
-- can only be defined by providers
drop table if exists slot_blueprints cascade;
create table slot_blueprints (
	id serial primary key,
	establishment_id bigint not null references establishments(id),
	weekdays INT not null check (weekdays > 0 and weekdays <= (B'1111111')::INT),
	reservationlimit int check(reservationlimit is null or reservationlimit > 0),
	fromdate date not null default NOW(), -- date after which slots are scheduled
	todate date 	-- date after which slots stop being scheduled
					-- if null slots are scheduled indefinitely
);

-- needed to be able to schedule periodic slots
-- can only be defined by providers
drop table if exists periodic_slot_blueprints cascade;
create table periodic_slot_blueprints (
	id serial primary key,
	slot_blueprint_id bigint not null references slot_blueprints(id)
		on update cascade
		on delete cascade,
	fromtime time not null,
	totime time not null check(totime > fromtime)
);

-- needed to be able to schedule manual slots
-- can only be defined by providers
drop table if exists manual_slot_blueprints cascade;
create table manual_slot_blueprints (
	id serial primary key,
	slot_blueprint_id bigint not null references slot_blueprints(id)
		on update cascade
		on delete cascade,
	opentime time not null,
	closetime time not null check (closetime > opentime),
	maxduration interval not null check(maxduration < (closetime - opentime))
);

-- these are periodic slot instances
-- can be instantiated and owned by any user type
drop table if exists periodic_slots cascade;
create table periodic_slots (
	id serial primary key,
	slot_id bigint not null references slots(id)
		on update cascade
		on delete cascade,
	periodic_slot_blueprint_id bigint not null references periodic_slot_blueprints(id)
		on delete cascade
		on update cascade
);

-- these are manual slot instances
-- can be instantiated and owned by any user type
-- the provider may want to give some degree of liberty to users when it comes to time and duration of slots
-- when a client makes a reservation for a manual slot two things happen:
--		1) a manual_slot instance with the desired starting and closing time is created
--		2) a reservation is added for that slot
-- depending on the relative base blueprint, other users may now be able to make reservations for that same slot instance
drop table if exists manual_slots cascade;
create table manual_slots (
	id serial primary key,
	slot_id bigint not null references slots(id)
		on update cascade
		on delete cascade,
	manual_slot_blueprint_id bigint not null references manual_slot_blueprints(id)
		on update cascade
		on delete cascade,
	fromtime time not null,
	totime time not null check(totime > fromtime)
);
