To run: java -cp windowing-1.0.jar windowing.windowing username password databaseToProcess start end totalQuantity startingResultsIndex

NOTES: This program finds common atom-atom interactions across pre-loaded OpenContact mapping data in a DB across a user-specified window whose starting and ending snapshots comprising the window are increased by one with the goal of ultimately providing a time domain profile of how the quantity of common interactions change versus time across all snapshots.

"username" and "password" are credentials to locally installed database, "databaseToProcess" is database containing snapshots you want to window over, "start" is first snapshot to define the window, "end" is the last snapshot to define the window ("start" and "end" together define the window size you want to use), "totalQuantity" is the total quantity of snapshots in the entire database and "startingResultsIndex" is the first index number for the "results" table housing the commons results over the very first window (subsequent windows and therefore subsequent "startingResultsIndex" will of course be incremented by the program).

Remember, table names and results tables would need to be renamed in "FlexibleJoin" class if you need multiple windowing analyses in the same database and to avoid duplicate table names (which will throw runtime errors).
These are the names that would need to be changed in the "FlexibleJoin" class: "20_master_windows_results" and 
"20theResults" - if an analysis dealing with a window composed of 20 snapshots was first made in the database 
housing your snapshot data and you then wanted to do an analysis in that same database with those same 
snapshots, but dealing with windows composed of 50 snapshots, doing such a name change is necessary to give 
the new results tables different names so they don't overlap with the results tables' names of the previous 20 
snapshot window analysis.
