-------------------------------
-- no mixed blueprint policies
-------------------------------

insert into periodic_slot_blueprints(slot_blueprint_id, fromtime, totime)
values (
	(select id from slot_blueprints where reservationlimit is null),
	'06:00', '07:00'
);

update periodic_slot_blueprints
set slot_blueprint_id = 2
where id = 1;

update slot_blueprints
set establishment_id = 2
where id = 1;


----------------------
-- no overlapping slot_blueprints
----------------------


-- manual

insert into periodic_slot_blueprints (slot_blueprint_id, fromtime, totime)
values (
	(select id from slot_blueprints where reservationlimit = 15),
	'11:00', '11:30'
);


-- periodic

insert into manual_slot_blueprints (slot_blueprint_id, opentime, closetime, maxduration)
values (
	(select id from slot_blueprints where reservationlimit is null),
	'12:00', '15:45', '01:00'
);
