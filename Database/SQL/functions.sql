create or replace function timeframes_overlap(f0 time, t0 time, f1 time, t1 time)
    returns boolean
    language plpgsql
as $$
begin
	return (f0 = f1) or (f0 < f1 and t0 > f1) or (f0 > f1 and f0 < t1);
end;
$$;


create or replace function timeframes_overlap(f0 date, t0 date, f1 date, t1 date)
    returns boolean
    language plpgsql
as $$
begin
	return (f0 = f1) or (f0 < f1 and t0 > f1) or (f0 > f1 and f0 < t1);
end;
$$;

create or replace function is_date_between(in_date date, from_date date, to_date date)
	returns boolean
	language plpgsql
as $$
begin
	return in_date >= from_date and (in_date < to_date or to_date is null);
end;$$;


create or replace function blueprints_overlap(a slot_blueprints, b slot_blueprints)
    returns boolean
    language plpgsql
as $$
begin
	if
		(a.id = b.id)
		or 
		(
			(a.establishment_id = b.establishment_id)
			and
			(0 <> (a.weekdays & b.weekdays))
			and
			timeframes_overlap(
				a.fromdate,
				a.todate,
				b.fromdate,
				b.todate
			)
		)
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function blueprints_overlap(a periodic_slot_blueprints, b periodic_slot_blueprints)
    returns boolean
    language plpgsql
as $$
declare
	base_a slot_blueprints;
	base_b slot_blueprints;
begin
	select * into base_a from slot_blueprints where id = a.slot_blueprint_id;
	select * into base_b from slot_blueprints where id = b.slot_blueprint_id;

	if
		(a.id = b.id)
		or
		(
			blueprints_overlap(base_a, base_b)
			and
			timeframes_overlap(
				a.fromtime,
				a.totime,
				b.fromtime,
				b.totime
			)
		)
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function blueprints_overlap(a manual_slot_blueprints, b manual_slot_blueprints)
    returns boolean
    language plpgsql
as $$
declare
	base_a slot_blueprints;
	base_b slot_blueprints;
begin
	select * into base_a from slot_blueprints where id = a.slot_blueprint_id;
	select * into base_b from slot_blueprints where id = b.slot_blueprint_id;

	if 
		(a.id = b.id)
		or 
		(
			blueprints_overlap(base_a, base_b)
			and
			timeframes_overlap (
				a.opentime,
				a.closetime,
				b.opentime,
				b.closetime
			)
		)
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function slots_overlap(a periodic_slots, b periodic_slots)
    returns boolean
    language plpgsql
as $$
declare
    base_a slots;
	base_b slots;
begin

	select * into base_a from slots where id = a.slot_id;
	select * into base_b from slots where id = b.slot_id;

	if base_a.date = base_b.date and a.periodic_slot_blueprint_id = b.periodic_slot_blueprint_id
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function slots_overlap(a manual_slots, b manual_slots)
    returns boolean
    language plpgsql
as $$
declare
    base_a slots;
	base_b slots;
begin
	select * into base_a from slots where id = a.slot_id;
	select * into base_b from slots where id = b.slot_id;

	if
		base_a.date = base_b.date
		and
		a.manual_slot_blueprint_id = b.manual_slot_blueprint_id
		and
		timeframes_overlap(
			a.fromtime,
			a.totime,
			b.fromtime,
			b.totime
		)
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function get_base_blueprint_by_slot_id(base_slot_id int)
	returns slot_blueprints
	language plpgsql
as $$
declare
	base_blueprint slot_blueprints;
begin
	select		b.*
	into		base_blueprint
	from		slots s
				join periodic_slots ps on ps.slot_id = s.id
				join periodic_slot_blueprints pb on pb.id = ps.periodic_slot_blueprint_id
				join slot_blueprints b on b.id = pb.slot_blueprint_id
	where		s.id = base_slot_id;

	if base_blueprint.id is NULL
	then
		select		b.*
		into		base_blueprint
		from		slots s
					join manual_slots ms on ms.slot_id = s.id
					join manual_slot_blueprints mb on mb.id = ms.manual_slot_blueprint_id
					join slot_blueprints b on b.id = mb.slot_blueprint_id
		where		s.id = base_slot_id;
	end if;

	return base_blueprint;
end;$$;


create or replace function get_coordinates_distance(lat0 float, lng0 float, lat1 float, lng1 float)
	returns float
	language plpgsql
as $$
declare
	p real = PI() / 180;
begin
	return 0.5 - c((lat1 - lat0) * p)/2 + 
          c(lat0 * p) * c(lat1 * p) * 
          (1 - c((lng1 - lng0) * p))/2;
end;$$;


create or replace function get_remaining_reservations(base_slot_id int)
	returns int
	language plpgsql
as $$
declare
	reservation_limit int;
	current_reservations int;
begin
	select		reservationlimit
	from		get_base_blueprint_by_slot_id(base_slot_id)
	into		reservation_limit;

	select		count(*)
	into		current_reservations
	from		reservations r
				join slots s on s.id = r.slot_id
	where		s.id = base_slot_id;

	return reservation_limit - current_reservations;
end;$$;


create or replace function has_establishment_periodic_policy(establishmentid int)
	returns boolean
	language plpgsql
as $$
declare
	is_periodic boolean;
begin
	is_periodic = exists (
		select		*
		from		slot_blueprints b
					join periodic_slot_blueprints p on p.slot_blueprint_id = b.id
		where		b.establishment_id = establishmentid
	);

	if is_periodic and exists (
		select		*
		from		slot_blueprints b
					join manual_slot_blueprints m on m.slot_blueprint_id = b.id
		where		b.establishment_id = establishmentid
	)
	then
		return null;
	end if;

	return is_periodic;
end;$$;
