import sqlalchemy as sql
import os
from sqlalchemy import Column, ForeignKey, create_engine, MetaData, Table, Column
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy.ext.automap import automap_base
from sqlalchemy.ext.declarative import declarative_base

HOST_VAR_NAME = 'MLB_DB_HOST'
PWD_VAR_NAME = 'MLB_DB_PWD'
host = os.environ.get(HOST_VAR_NAME)
pwd = os.environ.get(PWD_VAR_NAME)

if host is None or pwd is None:
	raise Exception(f'\'{HOST_VAR_NAME}\' and \'{PWD_VAR_NAME}\' environment variables are required to run this script')

engine = create_engine(f'postgresql://admin:{pwd}@{host}:5432/mylocalbooking_db', echo=True)

metadata = MetaData(bind=engine)

Session = sessionmaker(bind=engine)

DeclBase = declarative_base()

reservations_table = Table('reservations', DeclBase.metadata,
    Column('slot_id', ForeignKey('slots.id')),
    Column('client_id', ForeignKey('clients.id'))
)

Base = automap_base(DeclBase)

class AppUser(Base):
	__tablename__ = 'app_users'

	client = relationship(
		"Client",
		back_populates='appUser',
        uselist=False
	)

	provider = relationship(
		"Provider",
		back_populates='appUser',
        uselist=False
	)

	ownedSlots = relationship(
		"Slot",
		back_populates='owner',
        uselist=True
	)

class Client(Base):
	__tablename__ = 'clients'

	appUser = relationship(
		"AppUser",
		back_populates='client',
        uselist=False
	)

	bookedSlots = relationship(
		"Slot",
		secondary=reservations_table,
		secondaryjoin='Slot.id==reservations.slot_id',
		primaryjoin='Client.id==reservations.client_id',
		back_populates='reservations',
		uselist=True
	)

class Provider(Base):
	__tablename__ = 'providers'

	appUser = relationship(
		"AppUser",
		back_populates='provider',
        uselist=False
	)

	establishments = relationship(
		"Establishment",
		back_populates='provider',
		uselist=True
	)

class Establishment(Base):
	__tablename__ = 'establishments'

	provider = relationship(
		"Provider",
		back_populates='establishments',
		uselist=False
	)

	blueprints = relationship(
		"SlotBlueprint",
		back_populates='establishment',
		uselist=True
	)

class SlotBlueprint(Base):
	__tablename__ = 'slot_blueprints'

	establishment = relationship(
		"Establishment",
		back_populates='blueprints',
		uselist=False
	)

	periodicBlueprints = relationship(
		"PeriodicBlueprint",
		back_populates='blueprint',
		uselist=True
	)

	manualBlueprints = relationship(
		"ManualBlueprint",
		back_populates='blueprint',
		uselist=True
	)

class Slot(Base):
	__tablename__ = 'slots'

	owner = relationship(
		"AppUser",
		back_populates='ownedSlots',
		uselist=False
	)

	reservations = relationship(
		"Client",
		secondary=reservations_table,
		primaryjoin='Slot.id==reservations.slot_id',
		secondaryjoin='Client.id==reservations.client_id',
		back_populates='bookedSlots',
		uselist=True
	)

	manualSlot = relationship(
		"ManualSlot",
		back_populates='baseSlot',
		uselist = False
	)

	periodicSlot = relationship(
		"PeriodicSlot",
		back_populates='baseSlot',
		uselist = False
	)

class PeriodicBlueprint(Base):
	__tablename__ = 'periodic_slot_blueprints'

	blueprint = relationship(
		"SlotBlueprint",
		back_populates='periodicBlueprints',
		uselist=False
	)

	periodicSlots = relationship(
		"PeriodicSlot",
		back_populates='blueprint',
		uselist = True
	)

class ManualBlueprint(Base):
	__tablename__ = 'manual_slot_blueprints'

	blueprint = relationship(
		"SlotBlueprint",
		back_populates='manualBlueprints',
		uselist=False
	)

	manualSlots = relationship(
		"ManualSlot",
		back_populates='blueprint',
		uselist=True
	)

class ManualSlot(Base):
	__tablename__ = 'manual_slots'

	baseSlot = relationship(
		"Slot",
		back_populates='manualSlot',
		uselist = False
	)

	blueprint = relationship(
		"ManualBlueprint",
		back_populates='manualSlots',
		uselist=False
	)

class PeriodicSlot(Base):
	__tablename__ = 'periodic_slots'

	baseSlot = relationship(
		"Slot",
		back_populates='periodicSlot',
		uselist = False
	)

	blueprint = relationship(
		"PeriodicBlueprint",
		back_populates='periodicSlots',
		uselist = False
	)

class Blacklist(Base):
	__tablename__ = 'blacklists'

	provider = relationship(
		"Provider",
		backref='blacklist',
		uselist=False
	)


class Strike(Base):
	__tablename__ = 'strikes'

	provider = relationship(
		"Provider",
		backref='strike',
		uselist=False
	)


class Ratings(Base):
	__tablename__ = 'ratings'

	client = relationship(
		"Client",
		backref='rated'
	)

	establishment = relationship(
		"Establishment",
		backref='ratings'
	)


def generate_relationships(base, direction, return_fn, attrname, local_cls, referred_cls, **kw):
    return None
Base.prepare(engine=engine, reflect=True, generate_relationship=generate_relationships)