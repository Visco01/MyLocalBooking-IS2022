-------------------------------
-- no mixed blueprint policies
-------------------------------

-- manual blueprints already exists
-- either inserting a periodic blueprint
-- or matching a preexisting periodic blueprint (for a different establishment)
-- which conflicts with the original establishment's blueprint policy
create or replace function trg_no_mixed_blueprints_periodic()
	returns trigger
	language plpgsql
as $$
declare
	establishment_id int;
begin
	select		b.establishment
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.id;

	if exists (
		select		*
		from		manualslotblueprints m
					join slotblueprints b on b.id = m.id
		where		b.establishment = establishment_id
	)
	then 
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on periodicslotblueprints;
create trigger no_mixed_blueprints
before insert or update on periodicslotblueprints
for each row
execute function trg_no_mixed_blueprints_periodic();


-- periodic blueprints already exists
-- either inserting a manual blueprint
-- or matching a preexisting manual blueprint (for a different establishment)
-- which conflicts with the original establishment's blueprint policy
create or replace function trg_no_mixed_blueprints_manual()
	returns trigger 
	language plpgsql
as $$
declare
	establishment_id int;
begin
	select		b.establishment
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.id;

	if exists (
		select		*
		from		periodicslotblueprints p
					join slotblueprints b on b.id = p.id
		where		b.establishment = establishment_id
	)
	then
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on manualslotblueprints;
create trigger no_mixed_blueprints
before insert or update on manualslotblueprints
for each row
execute function trg_no_mixed_blueprints_manual();


-- updating a generic blueprint's establishment, which may conflict
-- with the original ones' blueprint policy
create or replace function trg_no_mixed_blueprints()
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
					join slotblueprints b on b.id = m.id
		where		b.establishment = NEW.establishment
	)
	or manual and exists (
		select		*
		from		periodicslotblueprints p
					join slotblueprints b on b.id = p.id
		where		b.establishment = NEW.establishment
	)
	then
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on slotblueprints;
create trigger no_mixed_blueprints
before update on slotblueprints
for each row
execute function trg_no_mixed_blueprints();




-------------------------------
-- no overlapping blueprints
-------------------------------


create or replace function trg_no_overlapping_blueprints_periodic()
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
		raise 'Blueprint overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_blueprints on periodicslotblueprints;
create trigger no_overlapping_blueprints
before insert or update on periodicslotblueprints
for each row
execute function trg_no_overlapping_blueprints_periodic();



create or replace function trg_no_overlapping_blueprints_manual()
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
		raise 'Blueprint overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_blueprints on manualslotblueprints;
create trigger no_overlapping_blueprints
before insert or update on manualslotblueprints
for each row
execute function trg_no_overlapping_blueprints_manual();


-------------------------------
-- no overlapping slots
-------------------------------

create or replace function trg_no_overlapping_slots_periodic()
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
		raise 'Slot overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_slots on periodicslots;
create trigger no_overlapping_slots
before insert or update on periodicslots
for each row
execute function trg_no_overlapping_slots_periodic();


create or replace function trg_no_overlapping_slots_manual()
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
		raise 'Slot overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_slots on manualslots;
create trigger no_overlapping_slots
before insert or update on manualslots
for each row
execute function trg_no_overlapping_slots_manual();



-------------------------------
-- slots' date should be compatibile with their blueprint's date window
-------------------------------

create or replace function trg_slot_date_in_date_window()
	returns trigger
	language plpgsql
as $$
declare
	isperiodic boolean;
	blueprintid int;
	from_date date;
	to_date date;
begin
	isperiodic = exists (select * from periodicslots p where p.blueprint = NEW.id);
	if isperiodic
	then
		select		pb.id
		into		blueprintid
		from		periodicslots p
					join periodicslotblueprints pb on pb.id = p.blueprint
		where		p.id = NEW.id;
	else
		select		mb.id
		into		blueprintid
		from		manualslots p
					join manualslotblueprints mb on mb.id = p.blueprint
		where		p.id = NEW.id;
	end if;

	select fromdate, todate into from_date, to_date from slotblueprints where id = blueprintid;

	if not (NEW.date between from_date and to_date)
	then
		if isperiodic
		then
			delete from periodicslots where id = NEW.id;
		else
			delete from manualslots where id = NEW.id;
		end if;

		raise 'Slot date does not fit its base blueprint''s recurrence time window';
	end if;

	return NULL;
end;$$;

drop trigger if exists slot_date_in_date_window on slots;
create constraint trigger slot_date_in_date_window
after insert or update on slots
deferrable initially deferred
for each row
execute function trg_slot_date_in_date_window();




create or replace function trg_slot_date_in_date_window_sub()
	returns trigger
	language plpgsql
as $$
declare
	slot_date date;
	from_date date;
	to_date date;
begin
	select date into slot_date from slots where id = NEW.id;
	select fromdate, todate into from_date, to_date from slotblueprints where id = NEW.blueprint;

	if slot_date between from_date and to_date
	then
		return NEW;
	end if;

	raise 'Slot date does not fit its base blueprint''s recurrence time window';
	return NULL;
end;$$;

drop trigger if exists slot_date_in_date_window on periodicslots;
create trigger slot_date_in_date_window
before insert or update on periodicslots
for each row
execute function trg_slot_date_in_date_window_sub();

drop trigger if exists slot_date_in_date_window on manualslots;
create trigger slot_date_in_date_window
before insert or update on manualslots
for each row
execute function trg_slot_date_in_date_window_sub();




-------------------------------
-- manual slots should fit their blueprint's time frame
-------------------------------

create or replace function trg_fit_blueprint_timeframe()
	returns trigger
	language plpgsql
as $$
declare
	open_time time;
	close_time time;
	max_duration time;

	slotduration interval = (NEW.totime - NEW.fromtime);
begin
	select		opentime, closetime, maxduration
	into		open_time, close_time, max_duration
	from		manualslotblueprints
	where		id = NEW.blueprint;

	if (NEW.fromtime < open_time) or (NEW.totime > close_time)
	then
		raise 'Manual slot does not fit its blueprint''s time frame';
		return NULL;
	end if;

	if (slotduration > max_duration)
	then
		raise 'Slot duration exceeds blueprint limit';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists fit_blueprint_timeframe on manualslots;
create trigger fit_blueprint_timeframe
before insert or update on manualslots
for each row
execute function trg_fit_blueprint_timeframe();


create or replace function trg_fit_blueprint_timeframe_blueprint()
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
		raise 'Slot does not fit its blueprint''s time frame';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists fit_blueprint_timeframe on manualslotblueprints;
create trigger fit_blueprint_timeframe
before update on manualslotblueprints
for each row
when (
	NEW.maxduration < OLD.maxduration or
	NEW.opentime > OLD.opentime or
	NEW.closetime < OLD.closetime
)
execute function trg_fit_blueprint_timeframe_blueprint();


-------------------------------
-- no unmatched generic slot blueprint
-------------------------------


create or replace function trg_no_unmatched_slot_blueprint()
	returns trigger 
	language plpgsql
as $$
begin
	if not exists (
		select * from periodicslotblueprints where id = NEW.id
	)
	and not exists (
		select * from manualslotblueprints where id = NEW.id
	)
	then
		delete from slotblueprints where id = NEW.id;
		raise 'Cannot insert an unmatched slotblueprint';
	end if;

	return NULL;
end;$$;


drop trigger if exists no_unmatched_slot_blueprint on slotblueprints;
create constraint trigger no_unmatched_slot_blueprint
after insert on slotblueprints
deferrable initially deferred
for each row
execute function trg_no_unmatched_slot_blueprint();




create or replace function trg_no_unmatched_slot_blueprint_sub()
	returns trigger
	language plpgsql
as $$
begin
	delete from slotblueprints where id = OLD.id;
	return NULL;
end;$$;


drop trigger if exists no_unmatched_slot_blueprint on periodicslotblueprints;
create trigger no_unmatched_slot_blueprint
after delete on periodicslotblueprints
for each row
execute function trg_no_unmatched_slot_blueprint_sub();

drop trigger if exists no_unmatched_slot_blueprint on manualslotblueprints;
create trigger no_unmatched_slot_blueprint
after delete on manualslotblueprints
for each row
execute function trg_no_unmatched_slot_blueprint_sub();



-------------------------------
-- no unmatched generic slot
-------------------------------

create or replace function trg_no_unmatched_slot()
	returns trigger
	language plpgsql
as $$
begin
	if not exists (
		select * from periodicslots where id = NEW.id
	)
	and not exists (
		select * from manualslots where id = NEW.id
	)
	then
		delete from slots where id = NEW.id;
		raise 'Cannot insert an unmatched slot';
	end if;

	return NULL;
end;$$;

drop trigger if exists no_unmatched_slot on slots;
create constraint trigger no_unmatched_slot
after insert on slots
deferrable initially deferred
for each row
execute function trg_no_unmatched_slot();


create or replace function trg_no_unmatched_slot_sub()
	returns trigger
	language plpgsql
as $$
begin
	delete from slots where id = OLD.id;
	return NULL;
end;$$;


drop trigger if exists no_unmatched_slot on periodicslots;
create trigger no_unmatched_slot
after delete on periodicslots
for each row
execute function trg_no_unmatched_slot_sub();

drop trigger if exists no_unmatched_slot on manualslots;
create trigger no_unmatched_slot
after delete on manualslots
for each row
execute function trg_no_unmatched_slot_sub();


-------------------------------
-- no unmatched appusers
-------------------------------

create or replace function trg_no_unmatched_appusers()
	returns trigger
	language plpgsql
as $$
begin
	if not exists (
		select * from clients where id = NEW.id
	)
	and not exists (
		select * from providers where id = NEW.id
	)
	then
		delete from appusers where id = NEW.id;
		raise 'Cannot insert an unmatched appuser';
	end if;

	return NULL;
end;$$;

drop trigger if exists no_unmatched_appusers on appusers;
create constraint trigger no_unmatched_appusers
after insert on appusers
deferrable initially deferred
for each row
execute function trg_no_unmatched_appusers();


create or replace function trg_no_unmatched_appusers_sub()
	returns trigger
	language plpgsql
as $$
begin
	delete from appusers where id = OLD.id;
	return NULL;
end;$$;


drop trigger if exists no_unmatched_appusers on clients;
create trigger no_unmatched_appusers
after delete on clients
for each row
execute function trg_no_unmatched_appusers_sub();

drop trigger if exists no_unmatched_appusers on providers;
create trigger no_unmatched_appusers
after delete on providers
for each row
execute function trg_no_unmatched_appusers_sub();



-------------------------------
-- reservations must follow reservation limits
-------------------------------

create or replace function trg_reservation_limit_slotblueprints()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		s.id
		from		periodicslots p
					join slots s on s.id = p.id
					join reservations r on r.slot = s.id
		where		p.blueprint = NEW.id
		group by	s.id
		having		count(*) > NEW.reservationlimit -- unknown -> false
	)
	or exists (
		select		s.id
		from		manualslots m
					join slots s on s.id = m.id
					join reservations r on r.slot = s.id
		where		m.blueprint = NEW.id
		group by	s.id
		having		count(*) > NEW.reservationlimit -- unknown -> false
	)
	then
		raise 'Already existing reservations exceed this reservation limit';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists reservation_limit on slotblueprints;
create trigger reservation_limit
before update on slotblueprints
for each row
when (NEW.reservationlimit < OLD.reservationlimit)
execute function trg_reservation_limit_slotblueprints();


create or replace function trg_reservation_limit()
	returns trigger
	language plpgsql
as $$
declare
	reservation_limit int;
begin
	if exists (select * from periodicslots where id = NEW.slot)
	then
		select		b.reservationlimit
		into		reservation_limit
		from		periodicslots p
					join slotblueprints b on b.id = p.blueprint;
	else
		select		b.reservationlimit
		into		reservation_limit
		from		manualslots m
					join slotblueprints b on b.id = m.blueprint;
	end if;

	if reservation_limit < ( -- unknown -> false
		select		count(*) + 1
		from		reservations
		where		slot = NEW.slot
	)
	then
		raise 'Reservation limit reached';
		return NULL;
	end if;

	return NEW;
end;$$;


drop trigger if exists reservation_limit on reservations;
create trigger reservation_limit
before insert or update on reservations
for each row
execute function trg_reservation_limit();



-------------------------------
-- deny reservations to blacklisted users
-------------------------------


create or replace function blacklisted_user_reservations()
	returns trigger
	language plpgsql
as $$
	user_cellphone char(12);
	provider_id int;
begin
	select cellphone into user_cellphone from appusers where id = NEW.client;
	
	select	e.owner
	into	provider_id
	from	get_base_blueprint_by_slot_id(NEW.slot) b
			join establishments e on e.id = b.establishment;

	if user_cellphone in (
		select		usercellphone
		from		blacklist
		where		provider = provider_id
	)
	then
		raise 'User is not allowed to make reservations for this slot';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists blacklisted_user_reservations on reservations;
create trigger blacklisted_user_reservations
before insert or update on reservations
for each row
execute function trg_blacklisted_user_reservations();



create function trg_blacklisted_user_drop_reservations()
	returns trigger
	language plpgsql
as $$
begin
	delete from reservations
	where sl
end;$$;

drop trigger if exists blacklisted_user_reservations on blacklist;
create trigger blacklisted_user_reservations
after delete on blacklist
for each row
execute function trg_blacklisted_user_drop_reservations();


