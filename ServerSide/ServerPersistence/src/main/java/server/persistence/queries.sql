/* Create a ChatHistory table.*/
CREATE TABLE ChatHistory
(
t_ID SERIAL PRIMARY KEY,
t_username TEXT,
t_message TEXT,
t_time TEXT 
);

/* Delete the ChatHistory table.*/
DROP TABLE ChatHistory;

/* Returns all entries in the ChatHistory table*/
Select * from ChatHistory

/* Store procedure to insert new rows in the CHatHistory table. */
/* If a stored procedure does not return any value, you can specify void as the return type. */
CREATE OR REPLACE FUNCTION addEntryInHistoryTable(_username_val TEXT, _message_val TEXT, _time_val TEXT) RETURNS void AS $$
	BEGIN
		INSERT INTO ChatHistory (t_username, t_message, t_time) VALUES (_username_val, _message_val, _time_val);
	END;
	$$ LANGUAGE plpgsql;

/* DROP the stored procedur. */
DROP FUNCTION addEntryInHistoryTable(text,text,date) 

/* Example to  Call the addEntryInHistoryTable procedure */
SELECT addEntryInHistoryTable('Costi1', 'mesajul meu foooooaaaarte luuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuung', 'Tue Jan 12 09:50:27 EET 2016');
SELECT addEntryInHistoryTable('Costi2', '\};|#@!', 'Tue Jan 12 09:50:27 EET 2016');
SELECT addEntryInHistoryTable('Costi3', '\};|#@!', 'Tue Jan 12 09:50:27 EET 2016');
SELECT addEntryInHistoryTable('Costi4', '\};|#@!', 'Tue Jan 12 09:50:27 EET 2016');
SELECT addEntryInHistoryTable('Costi5', '\};|#@!', 'Tue Jan 12 09:50:27 EET 2016');

/* Store procedure that displays everything in the ChatHistory table. */
/* To return one or more result sets (cursors in terms of PostgreSQL), you have to use refcursor return type. */
CREATE OR REPLACE FUNCTION displayAllEntriesInHistoryTable() RETURNS refcursor AS $$
	DECLARE	
		ref refcursor;
	BEGIN
		OPEN ref FOR SELECT * FROM ChatHistory; /* Open the cursor */
		RETURN ref;                             /* Return the cursor to the caller */
	END
	$$ LANGUAGE plpgsql;

/* DROP the stored procedur. */
DROP FUNCTION displayAllEntriesInHistoryTable(refcursor) 
DROP FUNCTION displayAllEntriesInHistoryTable() 

SELECT displayAllEntriesInHistoryTable();


/* Example to  call the displayAllEntriesInHistoryTable procedure. */
/* Start a transaction. */
BEGIN;
	SELECT displayAllEntriesInHistoryTable("history_cursor");
	FETCH ALL IN "history_cursor";
COMMIT;
