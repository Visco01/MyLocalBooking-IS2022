virtualenv .venv
echo "$(pwd)" > .venv/lib/python3.10/site-packages/mlblibs.pth
source .venv/bin/activate
pip install -r requirements.txt
