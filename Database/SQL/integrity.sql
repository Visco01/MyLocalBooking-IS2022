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
	select		b.establishment_id
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.slotblueprint_id;

	if exists (
		select		*
		from		manualslotblueprints m
					join slotblueprints b on b.id = m.slotblueprint_id
		where		b.establishment_id = establishment_id
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
	select		b.establishment_id
	into		establishment_id
	from		slotblueprints b
	where		b.id = NEW.slotblueprint_id;

	if exists (
		select		*
		from		periodicslotblueprints p
					join slotblueprints b on b.id = p.slotblueprint_id
		where		b.establishment_id = establishment_id
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
begin
	if exists (
		select		*
		from		slotblueprints s
					join manualslotblueprints m on m.slotblueprint_id = s.id
					join periodicslotblueprints p on p.slotblueprint_id = s.id
		where		s.establishment_id = NEW.establishment_id
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
		where		blueprints_overlap(p, NEW)
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
		where		blueprints_overlap(m, NEW)
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
		where		slots_overlap(p, NEW)
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
		where		slots_overlap(m, NEW)
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
	isperiodic = exists (select * from periodicslots p where p.slot_id = NEW.id);

	select fromdate, todate into from_date, to_date from get_base_blueprint_by_slot_id(NEW.id);

	if not (NEW.date between from_date and to_date)
	then
		if isperiodic
		then
			delete from periodicslots where slot_id = NEW.id;
		else
			delete from manualslots where slot_id = NEW.id;
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
	select date into slot_date from slots where id = NEW.slot_id;
	select fromdate, todate into from_date, to_date from get_base_blueprint_by_slot_id(NEW.slot_id);

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
	where		id = NEW.manualslotblueprint_id;

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
		where		m.manualslotblueprint_id = NEW.id and
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
		select * from periodicslotblueprints where slotblueprint_id = NEW.id
	)
	and not exists (
		select * from manualslotblueprints where slotblueprint_id = NEW.id
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
	with to_drop as (
		select		distinct b.id
		from		slotblueprints b
		where		not exists (select * from manualslotblueprints where slotblueprint_id = b.id) and
					not exists (select * from periodicslotblueprints where slotblueprint_id = b.id)
	)
	delete from slotblueprints where id in (select id from to_drop);
	return NULL;
end;$$;


drop trigger if exists no_unmatched_slot_blueprint on periodicslotblueprints;
create trigger no_unmatched_slot_blueprint
after delete on periodicslotblueprints
for each statement
execute function trg_no_unmatched_slot_blueprint_sub();

drop trigger if exists no_unmatched_slot_blueprint on manualslotblueprints;
create trigger no_unmatched_slot_blueprint
after delete on manualslotblueprints
for each statement
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
		select * from periodicslots where slot_id = NEW.id
	)
	and not exists (
		select * from manualslots where slot_id = NEW.id
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
	with to_drop as (
		select		distinct s.id
		from		slots s
		where		not exists (select * from manualslots where slot_id = s.id) and
					not exists (select * from periodicslots where slot_id = s.id)
	)
	delete from slots where id in (select id from to_drop);
	return NULL;
end;$$;


drop trigger if exists no_unmatched_slot on periodicslots;
create trigger no_unmatched_slot
after delete on periodicslots
for each statement
execute function trg_no_unmatched_slot_sub();

drop trigger if exists no_unmatched_slot on manualslots;
create trigger no_unmatched_slot
after delete on manualslots
for each statement
execute function trg_no_unmatched_slot_sub();


-------------------------------
-- no unmatched app_users
-------------------------------

create or replace function trg_no_unmatched_app_users()
	returns trigger
	language plpgsql
as $$
begin
	if not exists (
		select * from clients where app_user_id = NEW.id
	)
	and not exists (
		select * from providers where app_user_id = NEW.id
	)
	then
		delete from app_users where id = NEW.id;
		raise 'Cannot insert an unmatched appuser';
	end if;

	return NULL;
end;$$;

drop trigger if exists no_unmatched_app_users on app_users;
create constraint trigger no_unmatched_app_users
after insert on app_users
deferrable initially deferred
for each row
execute function trg_no_unmatched_app_users();


create or replace function trg_no_unmatched_app_users_sub()
	returns trigger
	language plpgsql
as $$
begin
	delete from app_users where id = OLD.app_user_id;
	return NULL;
end;$$;


drop trigger if exists no_unmatched_app_users on clients;
create trigger no_unmatched_app_users
after delete on clients
for each row
execute function trg_no_unmatched_app_users_sub();

drop trigger if exists no_unmatched_app_users on providers;
create trigger no_unmatched_app_users
after delete on providers
for each row
execute function trg_no_unmatched_app_users_sub();



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
		from		periodicslotblueprints pb
					join periodicslots p on p.periodicslotblueprint_id = pb.id
					join slots s on s.id = p.slot_id
					join reservations r on r.slot_id = s.id
		where		pb.slotblueprint_id = NEW.id
		group by	s.id
		having		count(*) > NEW.reservationlimit -- unknown -> false
	)
	or exists (
		select		s.id
		from		manualslotblueprints mb
					join manualslots m on m.periodicslotblueprint_id = mb.id
					join slots s on s.id = m.slot_id
					join reservations r on r.slot_id = s.id
		where		mb.slotblueprint_id = NEW.id
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
	select		reservationlimit
	into		reservation_limit
	from		get_base_blueprint_by_slot_id(NEW.slot_id);

	if reservation_limit < ( -- unknown -> false
		select		count(*) + 1
		from		reservations
		where		slot_id = NEW.slot_id
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
declare
	user_cellphone char(12);
	provider_id int;
begin
	select		a.cellphone
	into		user_cellphone
	from		clients c
				join app_users a on a.id = c.app_user_id
	where		c.id = NEW.client_id;
	
	select	e.provider_id
	into	provider_id
	from	get_base_blueprint_by_slot_id(NEW.slot_id) b
			join establishments e on e.id = b.establishment_id;

	if user_cellphone in (
		select		b.usercellphone
		from		blacklist b
		where		b.provider_id = provider_id
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
	where client_id in (
		select		c.id
		from		clients c
					join appusers a on a.id = c.app_user_id
		where		a.cellphone = NEW.usercellphone
	);
end;$$;

drop trigger if exists blacklisted_user_reservations on blacklist;
create trigger blacklisted_user_reservations
after insert or update on blacklist
for each row
execute function trg_blacklisted_user_drop_reservations();
