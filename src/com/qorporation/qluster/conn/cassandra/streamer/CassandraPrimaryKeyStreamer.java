package com.qorporation.qluster.conn.cassandra.streamer;

public interface CassandraPrimaryKeyStreamer<T> {
	
	public String generateKey();
	
}
