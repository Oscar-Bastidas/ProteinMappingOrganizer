To execute: java -cp bastidas-1.0-SNAPSHOT.jar promaporg.promap username password database_name starting ending

This program identifies which specific atom-atom interactions are found in common across all snapshots in a
supplied protein structure ensemble.  The specific input data to this program are the results from executing an 
Open Contact inter-chain mapping of each snapshot of the ensemble: if the ensemble had 100 snapshots, then
Open Contact should have been run 100 times, once on each individual snapshot.  Each snapshot's mapping yields
a "finedata.txt" file (which contains the mapping results data) which is then uploaded to your SQL database and then the present Java program can be run on all that data uploaded to your database.  The specific application for this 
program is for an ensemble generated from molecular dynamics simulations though ensembles from NMR are also 
adequate since there is also some degree of intra-protein stability captured by NMR ensembles.

NOTE: The "username" and "password" are for the user's local SQL installation, "database_name" is the name the user 
wishes to give the database containng the data and results, "starting" is the starting snapshot the user wants to start 
analyzing from and "ending" is the ending snapshot the user wants to analyze to
