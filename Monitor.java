
/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

public class Monitor {
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private enum STATE {
		THINKING, HUNGRY, EATING
	};

	// added the number of philosophers, an array to hold the states and a bool
	// value to check if the philosopher is talking. The priorityqueue is used to
	private int nbrPhilosopher;
	private STATE[] state;
	private boolean is_talking;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		nbrPhilosopher = piNumberOfPhilosophers;
		state = new STATE[nbrPhilosopher];

		// setting all philosophers to thinking
		for (int i = 0; i < nbrPhilosopher; i++) {
			state[i] = STATE.THINKING;
		}

		// no philosopher should talk at the start
		is_talking = false;
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	public synchronized void canEat(int pos) {
		try {
			while (true) {
				// we handle left and right hands here. (pos + 1) % 10 checks the philosophers
				// left hand
				// then we check the philosophers right hand by doing (pos + (nbrPhilosopher -
				// 1)) % nbrPhilosopher
				// in both of these instance we use the % nbrPhilosopher since we are handling a
				// roundtable (you can see it as a circular array)
				// and like a circular array, once you reach the end of the array, you need to
				// go back to the start, and we use % for that.

				// so basically what we are doing here is checking if left and right are not
				// eating, and if we are hungry, then we can start eating.
				if (state[(pos + 1) % nbrPhilosopher] != STATE.EATING
						&& state[(pos + (nbrPhilosopher - 1)) % nbrPhilosopher] != STATE.EATING
						&& state[pos] == STATE.HUNGRY) {
					state[pos] = STATE.EATING; // philosopher eats
					break;
				} else {
					wait(); // if you cannot eat, wait
				}
			}
		}

		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID) {
		// 1. find the position of the philosopher
		// 2. set that philosopher to hungry
		// 3. add the philosopher to the hungry list
		// 4. check if the philosopher can eat
		// 5. remove him from hungry list since state is eating now
		int pos = piTID - 1;
		state[pos] = STATE.HUNGRY;
		canEat(pos);
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		// 1. find the position
		// 2. done eating so goes back to thinking
		// 3. notify the other threads that they can try eating (we dont use notify
		// since we want to notify all threads not just one)
		int pos = piTID - 1;
		state[pos] = STATE.THINKING;
		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk() {
		// wait if someone is talking
		while (is_talking) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Someone is speaking");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}

			// the philosopher can talk
			is_talking = true;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk() {
		// the philosopher stopped talking
		is_talking = false;
		notifyAll();
	}
}

// EOF
