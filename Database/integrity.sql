-- reservation limit for each establishment
create function fnc_trg_reservation_limit()
    returns trigger
    language plpgsql
as $$
declare
    reservations_total int;
begin
    select      count(*) into reservations_total
    from        reservations
    where       establishments = NEW.establishment;
    
    
    if(reservations_total >= (select limit from establishments where id = NEW.establishment))
    then
        return NULL;
    end if;
    
    
    return NEW;      
end;$$;


create trigger reservation_limit
before insert or update on reservations
for each row
execute function fnc_trg_reservation_limit();