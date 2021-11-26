<time> <event> {<process id>}

time - integer representing the current system time measured in milliseconds

event:
	C - create
	E - exit
	R N - request resource N
	I N - interrupt resource number N
	T - Timer interrupt

Output - the time spent running, waiting, and blocked