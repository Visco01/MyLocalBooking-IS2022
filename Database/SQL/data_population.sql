----------------------------------
-- wipe tables
----------------------------------
--
delete from reservations;
delete from periodic_slots;
delete from manual_slots;
--
delete from slots;
--
delete from manual_slot_blueprints;
delete from periodic_slot_blueprints;
delete from slot_blueprints;
delete from establishments;
delete from clients;
delete from providers;
--
delete from app_users;

ALTER SEQUENCE app_users_id_seq RESTART WITH 1;
ALTER SEQUENCE blacklists_id_seq RESTART WITH 1;
ALTER SEQUENCE clients_id_seq RESTART WITH 1;
ALTER SEQUENCE establishments_id_seq RESTART WITH 1;
ALTER SEQUENCE manual_slot_blueprints_id_seq RESTART WITH 1;
ALTER SEQUENCE manual_slots_id_seq RESTART WITH 1;
ALTER SEQUENCE periodic_slot_blueprints_id_seq RESTART WITH 1;
ALTER SEQUENCE periodic_slots_id_seq RESTART WITH 1;
ALTER SEQUENCE providers_id_seq RESTART WITH 1;
ALTER SEQUENCE ratings_id_seq RESTART WITH 1;
ALTER SEQUENCE reservations_id_seq RESTART WITH 1;
ALTER SEQUENCE slot_blueprints_id_seq RESTART WITH 1;
ALTER SEQUENCE slots_id_seq RESTART WITH 1;
ALTER SEQUENCE strikes_id_seq RESTART WITH 1;
ALTER SEQUENCE users_id_seq RESTART WITH 1;


----------------------------------
-- creates one provider
----------------------------------

do
$$
declare
	app_user_id bigint;
	provider_id bigint;
begin
	insert into app_users(password_digest, cellphone, firstname, lastname, dob) values
	('', '393475322555', 'nicola', 'marizza', '10/06/00')
	returning id into app_user_id;

	insert into providers(app_user_id) values (app_user_id)
	returning id into provider_id;

	insert into establishments(provider_id, address, name, place_id, lat, lng) values
	(provider_id, 'via ciao', 'periodic establishment', '', 0, 0),
	(provider_id, 'via ciao', 'manual establishment', '', 0, 0);
end;$$;



----------------------------------
-- creates periodic slot blueprint
----------------------------------

do
$$
declare
    blueprintid bigint;
    
    weekdays int = (B'0000100')::int;
    reservationlimit int = 15;
    fromdate date = '10/06/21';
    todate date = '10/06/22';
begin
    insert into slot_blueprints(establishment_id, weekdays, reservationlimit, fromdate, todate) values
    ((select id from establishments where name ~ 'periodic'), weekdays, reservationlimit, fromdate, todate)
    returning id into blueprintid;
    
    insert into periodic_slot_blueprints(slot_blueprint_id, fromtime, totime) values
    (blueprintid, '10:00', '12:00'),
    (blueprintid, '12:00', '14:00');


    insert into slot_blueprints(establishment_id, weekdays, reservationlimit, fromdate, todate) values
    ((select id from establishments where name ~ 'manual'), weekdays, null, fromdate, todate)
    returning id into blueprintid;
    
    insert into manual_slot_blueprints(slot_blueprint_id, opentime, closetime, maxduration) values
    (blueprintid, '08:00', '12:00', '03:00');

    insert into slot_blueprints(establishment_id, weekdays, reservationlimit, fromdate, todate) values
    ((select id from establishments where name ~ 'manual'), weekdays, 50, fromdate, todate)
    returning id into blueprintid;
    
    insert into manual_slot_blueprints(slot_blueprint_id, opentime, closetime, maxduration) values
    (blueprintid, '15:30', '21:00', '03:00');
end;$$;

----------------------------------
-- creates some clients
----------------------------------
do
$$
declare
	base_cellphone char(12) = '391000000000';
	app_user_id bigint;
begin
	for i in 1..100 loop
		insert into app_users(password_digest, cellphone, firstname, lastname, dob) values
		('', (base_cellphone::int + i)::char(12), 'user_' || i::text, 'user_' || i::text, '10/06/00')
		returning id into app_user_id;

		insert into clients(app_user_id) values (app_user_id);
	end loop;
end;$$;


----------------------------------
-- creates periodic slots
----------------------------------
do
$$
declare
    periodic0_base_id bigint;
    periodic1_base_id bigint;
begin
    insert into slots(date, password_digest, app_user_id) values
    ('10/06/21', null, (select id from app_users where firstname = 'user_1'))
    returning id into periodic0_base_id;

    insert into slots(date, password_digest, app_user_id) values
    ('10/06/21', null, (select id from app_users where firstname = 'user_2'))
    returning id into periodic1_base_id;

    insert into periodic_slots(slot_id, periodic_slot_blueprint_id) values
    (periodic0_base_id, (select id from periodic_slot_blueprints where fromtime = '10:00')),
    (periodic1_base_id, (select id from periodic_slot_blueprints where fromtime = '12:00'));
end;$$;


----------------------------------
-- creates manual slots
----------------------------------

do
$$
declare
    periodic0_base_id bigint;
    periodic1_base_id bigint;
begin
    insert into slots(date, password_digest, app_user_id) values
    ('10/06/21', null, (select id from app_users where firstname = 'user_2'))
    returning id into periodic0_base_id;

    insert into slots(date, password_digest, app_user_id) values
    ('10/06/21', null, (select id from app_users where firstname = 'user_3'))
    returning id into periodic1_base_id;

    insert into manual_slots(slot_id, fromtime, totime, manual_slot_blueprint_id) values
    (periodic0_base_id, '09:15', '12:00', (select id from manual_slot_blueprints where opentime = '08:00')),
    (periodic1_base_id, '15:30', '16:45', (select id from manual_slot_blueprints where opentime = '15:30'));
end;$$;



----------------------------------
-- allocate reservations
----------------------------------

-- maxes out reservations with respect to the reservationlimit
-- if reservationlimit is null, makes reservations for all clients

do
$$
declare
    looped_slot record;
	slot_owner_id bigint;
begin
	-- for every slot
    FOR looped_slot IN
		select		s.id, (get_base_blueprint_by_slot_id(s.id)).reservationlimit, s.app_user_id as owner
		from		slots s
    LOOP
		-- TODO delete when implemented trigger -----------
		-- automatically adds a reservation when the slot owner is a client, and not the provider 
		select		c.id
		into        slot_owner_id
		from		app_users a
                    join clients c on c.app_user_id = a.id
		where		a.id = looped_slot.owner;

		if FOUND
		then
			insert into reservations(slot_id, client_id) values (looped_slot.id, slot_owner_id);
		end if;
		-----------------------------------------------------

		if looped_slot.reservationlimit is null 
		then
			insert into reservations(slot_id, client_id)
			select		looped_slot.id, c.id
			from		clients c
						join app_users a on a.id = c.app_user_id
			where		c.id <> slot_owner_id;
		else
			insert into reservations(slot_id, client_id)
			select		looped_slot.id, c.id
			from		clients c
						join app_users a on a.id = c.app_user_id
			where		c.id <> slot_owner_id
			limit		get_remaining_reservations(looped_slot.id);
		end if;
    END LOOP;
end;$$;
