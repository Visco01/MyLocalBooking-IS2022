----------------------------------
-- creates one provider
----------------------------------

do
$$
declare
	userid int;
	establishmentid int;
begin
	insert into appusers(password, cellphone, email, firstname, lastname, dob) values
	('', '393475322555', null, 'nicola', 'marizza', '10/06/00')
	returning id into userid;

	insert into providers values (userid, true, 3, null);

	insert into establishments(owner, address, name) values
	(userid, 'via ciao', 'campetto di basket')
	returning id into establishmentid;
end;$$;



----------------------------------
-- creates periodic slot blueprint
----------------------------------

do
$$
declare
    blueprintid int;
    
    establishment int = (select id from establishments);
    weekdays bit(7) = B'0000100';
    reservationlimit int;
    fromdate date = '10/06/21';
    todate date = '10/06/22';
    
    fromtime time = '10:00';
    totime time = '12:00';
begin
    insert into slotblueprints(establishment, weekdays, reservationlimit, fromdate, todate) values
    (establishment, weekdays, reservationlimit, fromdate, todate)
    returning id into blueprintid;
    
    insert into periodicslotblueprints(id, fromtime, totime) values
    (blueprintid, fromtime, totime);

end;$$;

----------------------------------
-- creates periodic slot
----------------------------------
do
$$
declare
    slotid int;
    
    date date = '09/06/21';
    
    owner int = (select id from appusers where email ~ 'marizza');
begin
    insert into slots(date, password, owner) values
    (date, null, owner)
    returning id into slotid;
    
    insert into periodicslots(id, blueprint) values
    (slotid, (select id from periodicslotblueprints));

end;$$;

