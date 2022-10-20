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

create trigger trg_no_mixed_blueprints
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


create trigger trg_no_mixed_blueprints
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


create trigger trg_no_mixed_blueprints
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
declare
begin
	
end;$$;


create trigger trg_no_overlapping_blueprints
before insert or update on periodicslotblueprints
for each row
execute function trg_no_overlapping_blueprints_periodic();