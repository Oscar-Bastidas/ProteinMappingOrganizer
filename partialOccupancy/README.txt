java -cp partialOccupancy-1.0-SNAPSHOT.jar partialOccupancy.partialOccupancy userName passWord dataBase start end totalQuantity resultsTableIndex windowFraction

This program finds interactions found in (for example) "less than or equal to" 50% of the specified windows

"start" is starting snapshot number, "end" is ending snapshot number, "totalQuantity" is total quantity of snapshots in whole set, "resultsTableIndex" is number portion of the name of the results table (very first table program should make), "windowFraction" is the quantity of windows the interactions need to be found in

This program finds interactions that are found in a certain fraction of snapshots in a pre-defined window.
Variable named "end" indicates window size and variable named "windowFraction" indicates the fractional
quantity of windows that the interactions must be found in.  Whether the interactions should be found in
that specific quantity of windows, or "less than or equal to" that fractional quantity is indicated in the ancillary
custom class, "IterativeUnion" and the string sql expression would need to be modified accordingly.
