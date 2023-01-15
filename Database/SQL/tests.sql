-------------------------------
-- no mixed blueprint policies
-------------------------------

insert into periodic_blueprints(blueprint_id, fromtime, totime)
values (
	(select id from blueprints where reservationlimit is null),
	'06:00', '07:00'
);

update periodic_blueprints
set blueprint_id = 2
where id = 1;

update blueprints
set establishment_id = 2
where id = 1;


----------------------
-- no overlapping blueprints
----------------------


-- manual

insert into periodic_blueprints (blueprint_id, fromtime, totime)
values (
	(select id from blueprints where reservationlimit = 15),
	'11:00', '11:30'
);


-- periodic

insert into manual_blueprints (blueprint_id, opentime, closetime, maxduration)
values (
	(select id from blueprints where reservationlimit is null),
	'12:00', '15:45', '01:00'
);
