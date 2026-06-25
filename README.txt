This series of programs allows for inter-chain mapping data (having come from the Open Contact software) on a protein ensemble to be assessed for persistent atom-atom interactions and Jaccard index similarity metrics.

ProMapOrg identifies the persistent atom-atom interactions from the mapping data: "FlexibleJoin.java" contains string building for dynamic SQL table joins, and therefore this file contains actual SQL code which is manipulated by Java (i.e. any provided quantity of tables can be joined with no code alteration required)

windowing identifies the persistent atom-atom interactions from a series of sliding windows of varying sizes specified by the user

partialOccupancy identifies atom-atom interactions that are present below specieid thresholds, not necessarily persistent interactions (i.e. identifies atom-atom interactions present in less than or equal to 20 snapshots of sliding windows)
