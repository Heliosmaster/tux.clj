default: test

test:
	lein test

before:
	echo "Hello this is before"

after:
	echo "Hey this is after"
