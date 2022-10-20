create or replace function timeframes_overlap(f0 timestamp, t0 timestamp, f1 timestamp, t1 timestamp)
    returns boolean
    language plpgsql
as $$
begin
    return  f0 between f1 and t1 or
            t0 between f1 and t1 or
            f0 <= f1 and t0 >= t1;
end;
$$;

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


create or replace function blueprints_overlap(a slotblueprints, b slotblueprints)
    returns boolean
    language plpgsql
as $$
begin
	if 
		(0 <> (select weekdays from a) & (select weekdays from b))
		and
		(timeframes_overlap(
			(select fromdate::TIMESTAMP from a),
			(select todate::TIMESTAMP from a),
			(select fromdate::TIMESTAMP from b),
			(select todate::TIMESTAMP from b)
		))
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
	base_a periodicslotblueprints;
	base_b periodicslotblueprints;
begin
	select * into base_a from slotblueprints where id = a.blueprint;
	select * into base_b from slotblueprints where id = b.blueprint;

	if 
		(blueprints_overlap(base_a, base_b))
		and
		(timeframes_overlap(
			(select fromtime from a),
			(select totime from a),
			(select fromtime from b),
			(select totime from b)
		)) 
	then
		return NULL;
	end if;

	return NEW;
end;
$$;



create or replace function blueprints_overlap(a manualslotblueprints, b manualslotblueprints)
    returns boolean
    language plpgsql
as $$
declare
	base_a manualslotblueprints;
	base_b manualslotblueprints;
begin
	select * into base_a from slotblueprints where id = a.blueprint;
	select * into base_b from slotblueprints where id = b.blueprint;

	if 
		(blueprints_overlap(base_a, base_b))
		and
		(timeframes_overlap(
			(select fromtime from a),
			(select totime from a),
			(select fromtime from b),
			(select totime from b)
		)) 
	then
		return NULL;
	end if;

	return NEW;
end;
$$;
