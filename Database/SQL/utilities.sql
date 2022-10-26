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


create or replace function blueprints_overlap(a slotblueprints, b slotblueprints)
    returns boolean
    language plpgsql
as $$
begin
	if
		(a.id = b.id)
		or 
		(
			(a.establishment = b.establishment)
			and
			(0 <> (a.weekdays & b.weekdays)::int)
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


create or replace function blueprints_overlap(a periodicslotblueprints, b periodicslotblueprints)
    returns boolean
    language plpgsql
as $$
declare
	base_a slotblueprints;
	base_b slotblueprints;
begin
	select * into base_a from slotblueprints where id = a.id;
	select * into base_b from slotblueprints where id = b.id;

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



create or replace function blueprints_overlap(a manualslotblueprints, b manualslotblueprints)
    returns boolean
    language plpgsql
as $$
declare
	base_a slotblueprints;
	base_b slotblueprints;
begin
	select * into base_a from slotblueprints where id = a.id;
	select * into base_b from slotblueprints where id = b.id;

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

create or replace function slots_overlap(a periodicslots, b periodicslots)
    returns boolean
    language plpgsql
as $$
declare
    base_a slots;
	base_b slots;
begin

	select * into base_a from slots where id = a.slot_id;
	select * into base_b from slots where id = b.slot_id;

	if base_a.date = base_b.date and a.periodicslotblueprint_id = b.periodicslotblueprint_id
	then
		return TRUE;
	end if;

	return FALSE;
end;
$$;


create or replace function slots_overlap(a manualslots, b manualslots)
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
		a.manualslotblueprint_id = b.manualslotblueprint_id
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


create or replace function get_base_blueprint_by_slot_id(slot_id int)
	returns slotblueprints
	language plpgsql
as $$
declare
	base_blueprint slotblueprints;
begin
	select		b.*
	into		base_blueprint
	from		slots s
				join periodicslots ps on ps.slot_id = s.id
				join periodicslotblueprints pb on pb.id = ps.periodicslotblueprint_id
				join slotblueprints b on b.id = pb.slotblueprint_id
	where		s.id = slot_id;

	if base_blueprint.id is NULL
	then
		select		b.*
		into		base_blueprint
		from		slots s
					join manualslots ms on ms.slot_id = s.id
					join manualslotblueprints mb on mb.id = ms.manualslotblueprint_id
					join slotblueprints b on b.id = mb.slotblueprint_id
		where		s.id = slot_id;
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