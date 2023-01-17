per la configurazione delle librerie dello script eseguire:
pip install virtualenv
source setup.sh

script.py contiene solo le funzioni per generare i dati, da solo non genera niente.
prima di runnare lo script bisogna settare le variabili d'ambiente MLB_DB_HOST e MLB_DB_PWD con host e password del db.
esempio di utilizzo:

from script import *

generateProviders()
generateClients()
generateEstablishments()
generateManualBlueprints()
generatePeriodicBlueprints()
generateManualSlots()
generatePeriodicSlots()

with db.Session() as session:
    session.add_all(app_users)
    session.commit()
