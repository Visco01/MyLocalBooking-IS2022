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

engine = create_engine(f'postgresql://admin:{pwd}@{host}:5432/mylocalbooking_db')

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

class Client(Base):
	__tablename__ = 'clients'

	appUser = relationship(
		"AppUser",
		back_populates='client',
        uselist=False
	)

class Provider(Base):
	__tablename__ = 'providers'

	appUser = relationship(
		"AppUser",
		back_populates='provider',
        uselist=False
	)

class Establishment(Base):
	__tablename__ = 'establishments'

	provider = relationship(
		"Provider",
		backref='establishments'
	)

class SlotBlueprint(Base):
	__tablename__ = 'slot_blueprints'

	establishment = relationship(
		"Establishment",
		backref='blueprints'
	)

class Slot(Base):
	__tablename__ = 'slots'

	owner = relationship(
		"AppUser",
		backref='ownedSlots'
	)

	reservations = relationship(
		"Client",
		secondary=reservations_table,
		primaryjoin='Slot.id==reservations.slot_id',
		secondaryjoin='Client.id==reservations.client_id',
		backref='bookedSlots'
	)

class PeriodicBlueprint(Base):
	__tablename__ = 'periodic_slot_blueprints'

	blueprint = relationship(
		"SlotBlueprint",
		backref='periodicBlueprints'
	)

class ManualBlueprint(Base):
	__tablename__ = 'manual_slot_blueprints'

	blueprint = relationship(
		"SlotBlueprint",
		backref='manualBlueprints'
	)

class ManualSlot(Base):
	__tablename__ = 'manual_slots'

	baseSlot = relationship(
		"Slot",
		backref='manualSlots'
	)

	blueprint = relationship(
		"ManualBlueprint",
		backref='manualSlots'
	)

class PeriodicSlot(Base):
	__tablename__ = 'periodic_slots'

	baseSlot = relationship(
		"Slot",
		backref='periodicSlots'
	)

	blueprint = relationship(
		"PeriodicBlueprint",
		backref='periodicSlots'
	)

class Blacklist(Base):
	__tablename__ = 'blacklists'

	provider = relationship(
		"Provider",
		backref='blacklist'
	)


class Strike(Base):
	__tablename__ = 'strikes'

	provider = relationship(
		"Provider",
		backref='strike'
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