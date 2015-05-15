Nov 5 2014

Vaadin 7 update
---------------
To have mmowgli games use the latest code from Vaadin, we had to spend a considerable effort to alter the code
to use the Vaadin 7 api, which had changed in significant ways from prior versions.  Visit the Vaadin web site
to get a feeling for all the differences between V7 and V6.

One of the main areas which was different was in the handling of "sessions" for database access through the Hibernate
library. A Hibernate session is used to read, write and update database contents from Java, but in a Javaesque, object-
oriented way instead of using SQL statements. To eliminate the need for Hibernate api statements scattered throughout
the source, the standard way for access to Hibernate in prior versions was to hook into the Vaadin event code.
When a vaadin event is first handled, mmowgli code creates a single Hibernate session.  When database access is
performed during the course of that event, the same session is used, often in a convenient, invisible way.  On return
from main application code, the mmowgli code in the Vaadin event handler closes the session and commits the transaction.
So there is only one session create, open, commit and close in a single event.

Vaadin supported a different way of "tapping into" the Vaadin event system which would normally provide the same
functionality as described in the last paragraph.

Another of the main areas of difference between V7 and prior versions was the formal support (through a new
api) of "server push", which is the ability of code on the server to make changes in the users gui in the browser
without any action of the user (such as a button click, etc.).  Prior mmowglis made use of a 3rd party addon to 
Vaadin called ICEPush.  Using a supported, official api is always preferrable and it in itself was one of the reasons
to upgrade to version 7.

However, the new Hibernate session handling and the new server push api proved incompatible. Changing our code
to use the more Vaadin-accepted JPA plugin (an alternate ORM) would have taken significant effort.  Instead, the
"Hibernate session-per-event" design was scrapped and the following method was used.

Thread-Local Session Model
--------------------------
Database access through Hibernate in this version of Mmowgli is handled through a static (i.e., jvm-wide) class, HSess.java.
This class uses Java's "ThreadLocal" feature to store a single Hibernate session instance for used by any code executing in
the context of the chosen thread.  It is thread-safe, so other user sessions running in the same JVM but different threads
do not conflict.  It is used in the following way:

1.  At the entry points to mmowgli code from Vaadin ("system") code, the HSess.init() method is called to set up a session.
2.  Any mmowgli code running from that point on in the same thread may use the session by retrieving it through the Hsess.get() method.
3.  At the matching exit points in mmowgli code, the HSess.close() method is called to commit and close the session.

There are certain points where it is not clear if Vaadin system code is the caller or if a Hibernate thread-local session already exists.
In those cases, Object sessionKey = HSess.checkInit() may be used to conditionally start a new session.  It is paired with 
HSess.checkClose(sessionKey) to conditionally close the session.

Two conventions are available to help with session accounting:
1.  If a mmowgli method anywhere expects a thread-local session to exist, its name ends with "TL" (for "thread-local"). This means code
    in the method may use HSess.get() to get a session assuming that a caller method has already done HSess.init().  It also means that
    code in the method will and MUST not do a HSess.close(), which is also assumed to be done by the caller.  The rule is that any code
    calling a TL method must insure that HSess.init() has been called.
2.  There are "marker" annotations in the edu.nps.moves.mmowgli.markers package to annotate methods where a session is used. (The use
    of these is not yet consistent throughout the mmowgli code.)
    
"Out-of-band" methods
---------------------
In previous mmowgli versions which used the "session-per-event" hooks to establish Hibernate sessions, application code which ran from a
background thread or handled JMS message reception and needed to use the game database through Hibernate had to "manually" handle session
creation and closing.  Our naming convention for those method involved appending "oob" to the method name.  "Out-of-band" is a term from TCP
referring to meta-data which is sent outside of a connection, and the term is uses in a similar way here.

With the thread-local method of Hibernate session management, there is no distinction between oob and other methods as far as sessions are
concerted.  All oob methods use the TL mechanism directly or through associated methods.
