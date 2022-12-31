-------------------------------
-- tuple constraints
-------------------------------

alter table users 
	drop constraint if exists unique_name,
	alter column name set not null,
	alter column password_digest set not null,
	add constraint unique_name unique(name);

alter table app_users
	drop constraint if exists unique_cellphone,
	alter column password_digest set not null,
	alter column cellphone set not null,
	alter column firstname set not null,
	alter column lastname set not null,
	alter column dob set not null,
	add constraint unique_cellphone unique(cellphone);

alter table clients
	drop constraint if exists both_coordinates_provided,

	alter column app_user_id set not null,
	add constraint both_coordinates_provided check((lat is null and lng is null) or (lat is not null and lng is not null));

alter table providers
	drop constraint if exists maxstrikes_constraint,
	alter column app_user_id set not null,
	alter column isverified set not null,
	alter column isverified set default false,
	alter column maxstrikes set not null,
	alter column maxstrikes set default 1,
	add constraint maxstrikes_constraint check(maxstrikes > 0);

alter table blacklists
	drop constraint if exists one_per_client_provider_blacklists,
	alter column provider_id set not null,
	add constraint one_per_client_provider_blacklists unique(usercellphone, provider_id);

alter table strikes
	drop constraint if exists strike_count_constraint,
	drop constraint if exists one_per_client_provider_strikes,
	alter column provider_id set not null,
	alter column count set not null,
	alter column count set default 1,
	add constraint strike_count_constraint check(count > 0),
	add constraint one_per_client_provider_strikes unique(usercellphone, provider_id);

alter table establishments
	drop constraint if exists unique_address_name,
	alter column provider_id set not null,
	alter column name set not null,
	alter column place_id set not null,
	alter column address set not null,
	alter column lat set not null,
	alter column lng set not null,
	add constraint unique_address_name unique(address, name);


alter table ratings
	drop constraint if exists rating_constraint,
	alter column establishment_id set not null,
	alter column rating set not null,
	add constraint rating_constraint check (rating between 1 and 5);

alter table slots
	alter column date set not null,
	alter column app_user_id set not null;

alter table reservations
	drop constraint if exists one_per_client,
	alter column slot_id set not null,
	alter column client_id set not null,
	add constraint one_per_client unique(slot_id, client_id);

alter table slot_blueprints
	drop constraint if exists valid_limit,
	drop constraint if exists valid_weekdays,
	alter column establishment_id set not null,
	alter column weekdays set not null,
	alter column fromdate set not null,
	alter column fromdate set default NOW(),
	add constraint valid_limit check(reservationlimit is null or reservationlimit > 0),
	add constraint valid_weekdays check (weekdays > 0 and weekdays <= (B'1111111')::INT);

alter table periodic_slot_blueprints
	drop constraint if exists time_order,
	alter column slot_blueprint_id set not null,
	alter column fromtime set not null,
	alter column totime set not null,
	add constraint time_order check(totime > fromtime);

alter table manual_slot_blueprints 
	drop constraint if exists time_order,
	drop constraint if exists valid_duration,
	alter column slot_blueprint_id set not null,
	alter column opentime set not null,
	alter column closetime set not null,
	alter column maxduration set not null,
	add constraint time_order check (closetime > opentime),
	add constraint valid_duration check(maxduration <= (closetime - opentime));

alter table periodic_slots
	alter column slot_id set not null,
	alter column periodic_slot_blueprint_id set not null;

alter table manual_slots 
	drop constraint if exists time_order,
	alter column slot_id set not null,
	alter column manual_slot_blueprint_id set not null,
	alter column fromtime set not null,
	alter column totime set not null,
	add constraint time_order check(totime > fromtime);

-------------------------------
-- no mixed blueprint policies
-------------------------------


-- manual slot_blueprints already exists
-- either inserting a periodic blueprint
-- or matching a preexisting periodic blueprint (for a different establishment)
-- which conflicts with the original establishment's blueprint policy

create or replace function trg_no_mixed_blueprints_periodic()
	returns trigger
	language plpgsql
as $$
declare
	est_id bigint;
begin
	select		b.establishment_id
	into		est_id
	from		slot_blueprints b
	where		b.id = NEW.slot_blueprint_id;

	if exists (
		select		*
		from		manual_slot_blueprints m
					join slot_blueprints b on b.id = m.slot_blueprint_id
		where		b.establishment_id = est_id
	)
	then 
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	update establishments set has_periodic_policy = true where id = est_id;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on periodic_slot_blueprints;
create trigger no_mixed_blueprints
before insert or update on periodic_slot_blueprints
for each row
execute function trg_no_mixed_blueprints_periodic();


-- periodic slot_blueprints already exists
-- either inserting a manual blueprint
-- or changing a manual blueprint's base blueprint, which may point to a
-- different establishment with a conflicting blueprint policy

create or replace function trg_no_mixed_blueprints_manual()
	returns trigger 
	language plpgsql
as $$
declare
	est_id bigint;
begin
	select		b.establishment_id
	into		est_id
	from		slot_blueprints b
	where		b.id = NEW.slot_blueprint_id;

	if exists (
		select		*
		from		periodic_slot_blueprints p
					join slot_blueprints b on b.id = p.slot_blueprint_id
		where		b.establishment_id = est_id
	)
	then
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	update establishments set has_periodic_policy = false where id = est_id;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on manual_slot_blueprints;
create trigger no_mixed_blueprints
before insert or update on manual_slot_blueprints
for each row
execute function trg_no_mixed_blueprints_manual();


-- updating a generic blueprint's establishment which may have a
-- conflicting blueprint policy

create or replace function trg_no_mixed_blueprints()
	returns trigger
	language plpgsql
as $$
declare
	old_bp boolean = has_establishment_periodic_policy(OLD.establishment_id);
	new_bp boolean = has_establishment_periodic_policy(NEW.establishment_id);
begin
	if old_bp is distinct from new_bp
	then
		raise 'Mixed blueprint types are not allowed';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_mixed_blueprints on slot_blueprints;
create trigger no_mixed_blueprints
before update on slot_blueprints
for each row
when (OLD.establishment_id <> NEW.establishment_id)
execute function trg_no_mixed_blueprints();



-------------------------------
-- no overlapping slot_blueprints
-------------------------------


create or replace function trg_no_overlapping_blueprints_periodic()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		periodic_slot_blueprints p
		where		blueprints_overlap(p, NEW)
	)
	then
		raise 'Blueprint overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_blueprints on periodic_slot_blueprints;
create trigger no_overlapping_blueprints
before insert or update on periodic_slot_blueprints
for each row
execute function trg_no_overlapping_blueprints_periodic();


create or replace function trg_no_overlapping_blueprints_manual()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manual_slot_blueprints m
		where		blueprints_overlap(m, NEW)
	)
	then
		raise 'Blueprint overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_blueprints on manual_slot_blueprints;
create trigger no_overlapping_blueprints
before insert or update on manual_slot_blueprints
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
		from		periodic_slots p
		where		slots_overlap(p, NEW)
	)
	then
		raise 'Slot overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_slots on periodic_slots;
create trigger no_overlapping_slots
before insert or update on periodic_slots
for each row
execute function trg_no_overlapping_slots_periodic();


create or replace function trg_no_overlapping_slots_manual()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manual_slots m
		where		slots_overlap(m, NEW)
	)
	then
		raise 'Slot overlaps with existing one';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists no_overlapping_slots on manual_slots;
create trigger no_overlapping_slots
before insert or update on manual_slots
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
	blueprintid bigint;
	from_date date;
	to_date date;
begin
	select fromdate, todate into from_date, to_date from get_base_blueprint_by_slot_id(NEW.id);

	if not is_date_between(NEW.date, from_date, to_date)
	then
		if exists (select * from periodic_slots p where p.slot_id = NEW.id)
		then
			delete from periodic_slots where slot_id = NEW.id;
		else
			delete from manual_slots where slot_id = NEW.id;
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

	if not is_date_between(slot_date, from_date, to_date)
	then
		raise 'Slot date does not fit its base blueprint''s recurrence time window';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists slot_date_in_date_window on periodic_slots;
create trigger slot_date_in_date_window
before update on periodic_slots
for each row
execute function trg_slot_date_in_date_window_sub();

drop trigger if exists slot_date_in_date_window on manual_slots;
create trigger slot_date_in_date_window
before update on manual_slots
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
	from		manual_slot_blueprints
	where		id = NEW.manual_slot_blueprint_id;

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

drop trigger if exists fit_blueprint_timeframe on manual_slots;
create trigger fit_blueprint_timeframe
before insert or update on manual_slots
for each row
execute function trg_fit_blueprint_timeframe();


create or replace function trg_fit_blueprint_timeframe_blueprint()
	returns trigger
	language plpgsql
as $$
begin
	if exists (
		select		*
		from		manual_slots m
		where		m.manual_slot_blueprint_id = NEW.id and
					(m.totime - m.fromtime) > NEW.maxduration
	)
	then
		raise 'Slot does not fit its blueprint''s time frame';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists fit_blueprint_timeframe on manual_slot_blueprints;
create trigger fit_blueprint_timeframe
before update on manual_slot_blueprints
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
declare
	is_periodic boolean;
begin
	select		has_periodic_policy
	into 		is_periodic
	from		establishments
	where		id = NEW.establishment_id;

	if	is_periodic is null
		or is_periodic and not exists (select * from periodic_slot_blueprints where slot_blueprint_id = NEW.id)
		or not is_periodic and not exists (select * from manual_slot_blueprints where slot_blueprint_id = NEW.id)
	then
		delete from slot_blueprints where id = NEW.id;
		raise 'Cannot insert an unmatched blueprint';
	end if;

	return NULL;
end;$$;

drop trigger if exists no_unmatched_slot_blueprint on slot_blueprints;
create constraint trigger no_unmatched_slot_blueprint
after insert on slot_blueprints
deferrable initially deferred
for each row
execute function trg_no_unmatched_slot_blueprint();


create or replace function trg_no_unmatched_slot_blueprint_sub()
	returns trigger
	language plpgsql
as $$
declare
	is_periodic boolean;
begin
	select		e.has_periodic_policy
	into		is_periodic
	from		slot_blueprints b
				join establishments e on e.id = b.establishment_id
	where		b.id = OLD.slot_blueprint_id;

	if is_periodic and not exists (select * from periodic_slot_blueprints where slot_blueprint_id = OLD.slot_blueprint_id)
	or not is_periodic and not exists (select * from manual_slot_blueprints where slot_blueprint_id = OLD.slot_blueprint_id)
	then
		delete from slot_blueprints where id = OLD.slot_blueprint_id;
	end if;
	return NULL;
end;$$;

drop trigger if exists no_unmatched_slot_blueprint on periodic_slot_blueprints;
create trigger no_unmatched_slot_blueprint
after delete on periodic_slot_blueprints
for each statement
execute function trg_no_unmatched_slot_blueprint_sub();

drop trigger if exists no_unmatched_slot_blueprint on manual_slot_blueprints;
create trigger no_unmatched_slot_blueprint
after delete on manual_slot_blueprints
for each statement
execute function trg_no_unmatched_slot_blueprint_sub();


create or replace function trg_null_establishment_policy()
	returns trigger
	language plpgsql
as $$
declare
	is_periodic boolean;
	est_id bigint;
begin
	if not exists (select * from slot_blueprints where establishment_id = OLD.establishment_id)
	then
		update establishments set has_periodic_policy = NULL where id = est_id;
	end if;
	
	return NULL;
end;$$;

drop trigger if exists set_null_policy on slot_blueprints;
create trigger set_null_policy
after delete on slot_blueprints
for each row
execute function trg_null_establishment_policy();



-------------------------------
-- no unmatched generic slot
-------------------------------


create or replace function trg_no_unmatched_slot()
	returns trigger
	language plpgsql
as $$
begin
	if not exists (
		select * from periodic_slots where slot_id = NEW.id
	)
	and not exists (
		select * from manual_slots where slot_id = NEW.id
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
	delete from slots where id in (
		select		s.id
		from		slots s
		where		not exists (select * from manual_slots where slot_id = s.id) and
					not exists (select * from periodic_slots where slot_id = s.id)
	);
	return NULL;
end;$$;

drop trigger if exists no_unmatched_slot on periodic_slots;
create trigger no_unmatched_slot
after delete on periodic_slots
for each statement
execute function trg_no_unmatched_slot_sub();

drop trigger if exists no_unmatched_slot on manual_slots;
create trigger no_unmatched_slot
after delete on manual_slots
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


create or replace function trg_reservation_limit_blueprints()
	returns trigger
	language plpgsql
as $$
declare
	is_periodic boolean;
begin
	select		has_periodic_policy
	into		is_periodic
	from		establishments
	where		id = NEW.establishment_id;

	if is_periodic and exists (
		select		s.id
		from		periodic_slot_blueprints pb
					join periodic_slots p on p.periodic_slot_blueprint_id = pb.id
					join slots s on s.id = p.slot_id
					join reservations r on r.slot_id = s.id
		where		pb.slot_blueprint_id = NEW.id
		group by	s.id
		having		count(*) > NEW.reservationlimit -- unknown -> false
	)
	or not is_periodic and exists (
		select		s.id
		from		manual_slot_blueprints mb
					join manual_slots m on m.periodic_slot_blueprint_id = mb.id
					join slots s on s.id = m.slot_id
					join reservations r on r.slot_id = s.id
		where		mb.slot_blueprint_id = NEW.id
		group by	s.id
		having		count(*) > NEW.reservationlimit -- unknown -> false
	)
	then
		raise 'Already existing reservations exceed this reservation limit';
		return NULL;
	end if;

	return NEW;
end;$$;

drop trigger if exists reservation_limit on slot_blueprints;
create trigger reservation_limit
before update on slot_blueprints
for each row
when (NEW.reservationlimit < OLD.reservationlimit)
execute function trg_reservation_limit_blueprints();


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


create or replace function trg_blacklisted_user_reservations()
	returns trigger
	language plpgsql
as $$
declare
	user_cellphone char(13);
	prov_id bigint;
begin
	select		a.cellphone
	into		user_cellphone
	from		clients c
				join app_users a on a.id = c.app_user_id
	where		c.id = NEW.client_id;
	
	select	e.provider_id
	into	prov_id
	from	get_base_blueprint_by_slot_id(NEW.slot_id) b
			join establishments e on e.id = b.establishment_id;

	if user_cellphone in (
		select		b.usercellphone
		from		blacklists b
		where		b.provider_id = prov_id
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



-------------------------------
-- when reservation is deleted, slot instance should be deleted if there are no reservations left
-- otherwise slot ownership should be transferred to the establishment owner
-------------------------------


create or replace function trg_no_reservations_left()
	returns trigger
	language plpgsql
as $$
begin
	if not exists (
		select * from reservations where slot_id = OLD.slot_id
	)
	then
		delete from slots where id = OLD.slot_id;
	else
		update slots set app_user_id = (
			select		p.app_user_id
			from		get_base_blueprint_by_slot_id(OLD.slot_id) b
						join establishments e on e.id = b.establishment_id
						join providers p on p.id = e.provider_id
		);
	end if;

	return NULL;
end;$$;

drop trigger if exists no_reservations_left on reservations;
create trigger no_reservations_left
after delete on reservations
for each row
execute function trg_no_reservations_left();



-------------------------------
-- reservations of blacklisted users should be removed
-------------------------------


create or replace function trg_remove_reservation_on_blacklist()
	returns trigger
	language plpgsql
as $$
begin
	delete from reservations where
	slot_id in (
		select		s.id
		from		slots s
					cross join get_base_blueprint_by_slot_id(s.id) b
					join establishments e on e.id = b.establishment_id
		where		e.provider_id = NEW.provider_id
	)
	and client_id = (
		select		c.id
		from		app_users a
					join clients c on c.app_user_id = a.id
		where		a.cellphone = NEW.usercellphone
	);
end;$$;

drop trigger if exists remove_reservation_on_blacklist on blacklists;
create trigger remove_reservation_on_blacklist
after insert or update on blacklists
for each row
execute function trg_remove_reservation_on_blacklist();



-------------------------------
-- add user to blacklist when maxstrikes is reached
-------------------------------


create or replace function trg_maxstrikes_reached()
	returns trigger
	language plpgsql
as $$
begin
	if NEW.count = (
		select maxstrikes from providers where id = NEW.provider_id
	)
	then
		insert into blacklists(provider_id, usercellphone) values (NEW.provider_id, NEW.usercellphone);
		delete from strikes where id = NEW.id;
	end if;

	return NULL;
end;$$;

drop trigger if exists maxstrikes_reached on strikes;
create trigger maxstrikes_reached
after insert or update on strikes
for each row
execute function trg_maxstrikes_reached();


create or replace function trg_maxstrikes_reached_provider()
	returns trigger
	language plpgsql
as $$
begin
	update strikes
	set count = NEW.maxstrikes
	where provider_id = NEW.id and count >= NEW.maxstrikes;

	return NULL;
end;$$;

drop trigger if exists maxstrikes_reached on providers;
create trigger maxstrikes_reached
after update on providers
for each row
when (NEW.maxstrikes < OLD.maxstrikes)
execute function trg_maxstrikes_reached_provider();



-------------------------------
-- automatically add reservation when slot is added
-------------------------------


create or replace function trg_add_reservation()
	returns trigger
	language plpgsql
as $$
begin
	if not exists ( -- assert reservation doesn't already exist and owner is a client
		select		c.id
		into		client_id
		from		clients c
					join reservations r on r.client_id = c.id
		where		c.app_user_id = NEW.app_user_id and r.slot_id = NEW.id
	)
	then
		insert into reservations(slot_id, client_id) values
		(
			NEW.id,
			(select c.id from clients c where c.app_user_id = NEW.app_user_id)
		);
	end if;

	return NULL;
end;$$;


drop trigger if exists add_reservation on slots;
create trigger add_reservation
after insert or update on slots
for each row
execute function trg_add_reservation();