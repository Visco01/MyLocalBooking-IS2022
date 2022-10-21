create or replace function timeframes_overlap(f0 time, t0 time, f1 time, t1 time)
    returns boolean
    language plpgsql
as $$
begin
    return  f0 between f1 and t1 or
            t0 between f1 and t1 or
            f0 <= f1 and t0 >= t1;
end;
$$;

create or replace function timeframes_overlap(f0 date, t0 date, f1 date, t1 date)
    returns boolean
    language plpgsql
as $$
begin
    return  f0 between f1 and t1 or
            t0 between f1 and t1 or
            f0 <= f1 and t0 >= t1;
end;
$$;


create or replace function blueprints_overlap(a slotblueprints, b slotblueprints)
    returns boolean
    language plpgsql
as $$
begin
	if
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
		blueprints_overlap(base_a, base_b)
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
		blueprints_overlap(base_a, base_b)
		and
		timeframes_overlap (
			a.opentime,
			a.closetime,
			b.opentime,
			b.closetime
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
	select * into base_a from slots where id = a.id;
	select * into base_b from slots where id = b.id;

	if base_a.date = base_b.date and a.blueprint = b.blueprint
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
	select * into base_a from slots where id = a.id;
	select * into base_b from slots where id = b.id;

	if
		base_a.date = base_b.date
		and
		a.blueprint = b.blueprint
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

