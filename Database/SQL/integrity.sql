-------------------------------
-- no mixed blueprint policies
-------------------------------

-- manual blueprints already exists
-- either inserting a periodic blueprint
-- or matching a preexisting periodic blueprint (for a different establishment)
-- which conflicts with the original establishment's blueprint policy
create function trg_no_mixed_blueprints_periodic()
	returns trigger
	language plpgsql
as $$
declare
	establishment_id int;
begin
	select		b.establishment
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.blueprint;

	if exists (
		select		*
		from		manualslotblueprints m
					join slotblueprints b on b.id = m.blueprint
		where		b.establishment = establishment_id
	)
	then 
		return NULL;
	end if;

	return NEW;
end;$$;

create trigger no_mixed_blueprints
before insert or update on periodicslotblueprints
for each row
execute function trg_no_mixed_blueprints_periodic();


-- periodic blueprints already exists
-- either inserting a manual blueprint
-- or matching a preexisting manual blueprint (for a different establishment)
-- which conflicts with the original establishment's blueprint policy
create function trg_no_mixed_blueprints_manual()
	returns trigger 
	language plpgsql
as $$
declare
	establishment_id int;
begin
	select		b.establishment
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.blueprint;

	if exists (
		select		*
		from		periodicslotblueprints p
					join slotblueprints b on b.id = p.blueprint
		where		b.establishment = establishment_id
	)
	then 
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger no_mixed_blueprints
before insert or update on manualslotblueprints
for each row
execute function trg_no_mixed_blueprints_manual();


-- updating a generic blueprint's establishment, which may conflict
-- with the original ones' blueprint policy
create function trg_no_mixed_blueprints()
	returns trigger
	language plpgsql
as $$
declare
	manual boolean;
	periodic boolean;
begin
	manual = exists (select * from manualslotblueprints where id = NEW.id);
	periodic = exists (select * from periodicslotblueprints where id = NEW.id);

	if periodic and exists (
		select		*
		from		manualslotblueprints m
					join slotblueprints b on b.id = m.blueprint
		where		b.establishment = NEW.establishment
	)
	or manual and exists (
		select		*
		from		periodicslotblueprints p
					join slotblueprints b on b.id = p.blueprint
		where		b.establishment = NEW.establishment
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger no_mixed_blueprints
before update on slotblueprints
for each row
execute function trg_no_mixed_blueprints();




-------------------------------
-- no overlapping blueprints
-------------------------------


create function trg_no_overlapping_blueprints_periodic()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		periodicslotblueprints p
		where		p.id <> NEW.id and
					blueprints_overlap(p, NEW)
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger no_overlapping_blueprints
before insert or update on periodicslotblueprints
for each row
execute function trg_no_overlapping_blueprints_periodic();



create function trg_no_overlapping_blueprints_manual()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manualslotblueprints m
		where		m.id <> NEW.id and
					blueprints_overlap(m, NEW)
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger no_overlapping_blueprints
before insert or update on manualslotblueprints
for each row
execute function trg_no_overlapping_blueprints_manual();


create function trg_no_overlapping_blueprints()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		slotblueprints b
		where		b.id <> NEW.id and
					blueprints_overlap(b, NEW)
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger no_overlapping_blueprints
before insert or update on slotblueprints
for each row
execute function trg_no_overlapping_blueprints();



-------------------------------
-- no overlapping slots
-------------------------------


create function trg_no_overlapping_slots_periodic()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		periodicslots p
		where		p.id <> NEW.id and slots_overlap(p, NEW)
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;

create trigger no_overlapping_slots
before insert or update on periodicslots
for each row
execute function trg_no_overlapping_slots_periodic();


create function trg_no_overlapping_slots_manual()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manualslots m
		where		m.id <> NEW.id and slots_overlap(m, NEW)
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;

create trigger no_overlapping_slots
before insert or update on manualslots
for each row
execute function trg_no_overlapping_slots_manual();



-------------------------------
-- slots' date should be compatibile with their blueprint's date window
-------------------------------

create function trg_slot_date_in_date_window()
	returns trigger
	language plpgsql
as $$
	isperiodic boolean;
	blueprintid int;
	fromdate date;
	todate date;
begin
	isperiodic = (perform id from periodicslots where blueprint = NEW.id);
	if isperiodic
	then
		select		pb.id
		into		blueprintid
		from		periodicslots p
					join periodicslotblueprints pb on pb.id = p.blueprint
		where		p.id = NEW.id
	else
		select		mb.id
		into		blueprintid
		from		manualslots p
					join manualslotblueprints mb on mb.id = p.blueprint
		where		p.id = NEW.id
	end if;

	select fromdate, todate into fromdate, todate from slotblueprints where id = blueprintid;

	if not (NEW.date between fromdate and todate)
	then
		if isperiodic
		then
			delete from periodicslots where id = NEW.id;
		else
			delete from manualslots where id = NEW.id;
		end if;
	end if;

	return NULL;
end;$$;

create trigger slot_date_in_date_window
after insert or update on slots
for each row
deferrable initially deferred
execute function trg_slot_date_in_date_window();




create function trg_slot_date_in_date_window_sub()
	returns trigger
	language plpgsql
as $$
	slotdate date;
	fromdate date;
	todate date;
begin
	select date into slotdate from slots where id = NEW.id;
	select fromdate, todate into fromdate, todate from slotblueprints where id = NEW.blueprint;

	if slotdate between fromdate and todate
	then
		return NEW;
	end if;

	return NULL;
end;$$;


create trigger slot_date_in_date_window
before insert or update on periodicslots
for each row
execute function trg_slot_date_in_date_window_sub();

create trigger slot_date_in_date_window
before insert or update on manualslots
for each row
execute function trg_slot_date_in_date_window_sub();




-------------------------------
-- manual slots should fit their blueprint's time frame
-------------------------------

create function trg_fit_blueprint_timeframe()
	returns trigger
	language plpgsql
as $$
	opentime time;
	closetime time;
	maxduration time;

	slotduration = NEW.closetime - NEW.opentime;
begin
	select		opentime, closetime, maxduration
	into		opentime, closetime, maxduration
	from		manualslotblueprints
	where		id = NEW.blueprint;

	if (NEW.fromtime < opentime) or (NEW.totime > closetime) or (slotduration > maxduration)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger fit_blueprint_timeframe
before insert or update on manualslots
for each row
execute function trg_fit_blueprint_timeframe();


create function trg_fit_blueprint_timeframe_blueprint()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manualslots m
		where		m.blueprint = NEW.id and
					(m.totime - m.fromtime) > NEW.maxduration
	)
	then
		return NULL;
	end if;

	return NEW;
end;$$;


create trigger fit_blueprint_timeframe
before update on manualslotblueprints
for each row
when (
	NEW.maxduration < OLD.maxduration or
	NEW.fromtime > OLD.fromtime or
	NEW.totime < OLD.totime
)
execute function trg_fit_blueprint_timeframe_blueprint();

