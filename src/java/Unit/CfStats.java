package Unit;

public class CfStats {
	public  String ColumnFamily; 
	public  int SSTableCount;
	public  int SpaceUsedLive;
	public  int SpaceUsedTotal;
	public  int NumberOfKeys;
	public  int MemtableColumnsCount;
	public  int MemtableDataSize;
	public  int MemtableSwitchCount;
	public  int ReadCount;
	public  int ReadLatency;
	public  int WriteCount;
	public  int WriteLatency;
	public  int PendingTasks;
	public  int BloomFilterFalsePostives;
	public  float BloomFilterFalseRatio;
	public  int BloomFilterSpaceUsed;
	public  int KeyCacheCapacity;
	public  int KeyCacheSize;
	public  float KeyCacheHitRate;
	public  float RowCache;
	public  int CompactedRowMinimumSize;
	public  int CompactedRowMaximumSize;
	public  int CompactedRowMeanSize;
}
