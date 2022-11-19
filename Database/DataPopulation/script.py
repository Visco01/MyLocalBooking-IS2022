import db
from faker import Faker
from random import randint
from datetime import date, time, timedelta, datetime

N_CLIENTS = 200
N_PROVIDERS = 25
EMAIL_PERCENTAGE=60
POSITION_PERCENTAGE=30
RATING_COMMENTS_PERCENTAGE=20
COMPANY_PERCENTAGE=50
RESERVATION_LIMIT_PERCENTAGE=50
MAX_ESTABLISHMENTS_PER_PROVIDER=4
MAX_RESERVATION_LIMIT = 20
PERIODIC_PERCENTAGE = 50
MANUAL_DURATION_CAP_PERCENTAGE = 50
SLOT_GRANULARITY_MINUTES = 15
MAX_MANUAL_SLOT_DURATION_MULT = 20
MAX_PERIODIC_SLOT_DURATION_MULT = 8
MAX_DEAD_TIME_MULT = 16

fake = Faker(['it_IT'])
app_users = []
clients = []
providers = []
establishments = []
periodic_establishments = []
manual_establishments = []

periodic_blueprints = []
manual_blueprints = []

sports = ['Basket', 'Pallavolo', 'Atletica', 'Calcio', 'Padel', 'Tennis', 'Squash', 'Rugby']
place_types = ['Campo', 'Terreno', 'Aula', 'Sala', 'Conference room']


def generateEstablishmentName():
    placetype = fake.random_element(place_types)

    if placetype == 'Campo':
        placetype = f'{placetype} da {fake.random_element(sports)}'
    
    return f'{placetype} n° {randint(1, 6)}'


def testPercentage(percentage, trueval, falseval=None):
    if randint(1, 100) <= percentage:
        return trueval
    return falseval

def generateAppUser():
    firstname = fake.first_name()
    lastname = fake.last_name()
    email = testPercentage(EMAIL_PERCENTAGE, f'{firstname}.{lastname}@gmail.com')
    user = db.AppUser(
        password_digest=''.join(fake.random_letters(15)),
        cellphone=f'039{fake.msisdn()[3:]}',
        email=email,
        firstname=firstname,
        lastname=lastname,
        dob=fake.date(end_datetime=date(2002, 1, 1))
    )
    app_users.append(user)
    return user

def generateClients():
    for i in range(1, N_CLIENTS):
        location = testPercentage(POSITION_PERCENTAGE, (fake.latitude(), fake.longitude()), (None, None))

        client = db.Client(lat=location[0], lng=location[1])
        client.appUser = generateAppUser()
        clients.append(client)


def generateProviders():
    for i in range(1, N_PROVIDERS):
        provider = db.Provider(
            isverified=fake.boolean(), 
            maxstrikes=randint(1, 5),
            companyname=testPercentage(COMPANY_PERCENTAGE, fake.company() , None)
        )
        provider.appUser = generateAppUser()
        providers.append(provider)


def generateEstablishments():
    for prov in providers:
        for i in range(1, MAX_ESTABLISHMENTS_PER_PROVIDER + 1):
            est = db.Establishment(
                    name=generateEstablishmentName(),
                    place_id=f''.join(fake.random_letters(20)),
                    address=fake.address(),
                    lat=fake.latitude(),
                    lng=fake.longitude()
                )
            establishments.append(est)
            prov.establishments.append(est)

            if randint(1, 100) <= PERIODIC_PERCENTAGE:
                periodic_establishments.append(est)
            else:
                manual_establishments.append(est)

#def generateRatings():
#    for est in establishments:
#            comment = testPercentage(RATING_COMMENTS_PERCENTAGE, fake.paragraph(10), None)
#            est.ratings.add(db.Ratings(
#                rating=randint(1, 5),
#                comment=comment
#            ))


def generateBaseBlueprint():
    return db.SlotBlueprint (
        weekdays=randint(1, 127),
        reservationlimit=testPercentage(RESERVATION_LIMIT_PERCENTAGE, randint(1, MAX_RESERVATION_LIMIT), None),
        fromdate=fake.past_date(),
        todate=fake.future_date()
    )

def addToTime(timeval, deltaval):
    return (datetime(2000, 1, 1, timeval.hour, timeval.minute) + deltaval).time()

def timeframesOverlap(f0, t0, f1, t1):
    return (f0 == f1) or (f0 < f1 and t0 > f1) or (f0 > f1 and f0 < t1)

def isDateBetween(in_date, from_date, to_date):
    return in_date >= from_date and (in_date < to_date or to_date is None)

def baseBlueprintsOverlap(a, b):
	return ( 
            (a.establishment == b.establishment)
			and
			(0 != (a.weekdays & b.weekdays))
			and
			timeframesOverlap(
				a.fromdate,
				a.todate,
				b.fromdate,
				b.todate
			)
		)

def periodicBlueprintsOverlap(a, b):
    return (
        baseBlueprintsOverlap(a.blueprint, b.blueprint)
        and
        timeframesOverlap(
            a.fromtime,
            a.totime,
            b.fromtime,
            b.totime
        )
    )

def manualBlueprintsOverlap(a, b):
    return (
        baseBlueprintsOverlap(a.blueprint, b.blueprint)
        and
        timeframesOverlap (
            a.opentime,
            a.closetime,
            b.opentime,
            b.closetime
        )
    )


def generatePeriodicBlueprints():
    for est in periodic_establishments:
        base = generateBaseBlueprint()
        end = time(0, 0)
        start = time(8, 0)

        while start > end:
            end = addToTime(start, timedelta(minutes=randint(1, MAX_PERIODIC_SLOT_DURATION_MULT) * SLOT_GRANULARITY_MINUTES))

            if start < end:
                bp = db.PeriodicBlueprint(fromtime=start, totime=end)
                base.establishment = est
                bp.blueprint = base

                if len(list(filter(lambda b : periodicBlueprintsOverlap(b,bp), periodic_blueprints))) > 0:
                    bp.blueprint = None
                else:
                    periodic_blueprints.append(bp)
            
            start = addToTime(end, timedelta(minutes=randint(0, MAX_DEAD_TIME_MULT) * SLOT_GRANULARITY_MINUTES))

        if len(base.periodicBlueprints) == 0:
            base.establishment = None

