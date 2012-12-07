package com.qorporation.qluster.service.view;

import com.qorporation.qluster.annotation.AuthenticationPolicy.AuthenticationLevel;
import com.qorporation.qluster.conn.Connection;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.definition.User;

public interface ViewAuthenticator<U extends Definition<? extends Connection>> {

	public boolean authenticate(AuthenticationLevel level, Entity<U> user);
	public Entity<U> fromToken(String key, String token);
	
	public static class DefaultViewAuthenticator implements ViewAuthenticator<User> {

		@Override
		public boolean authenticate(AuthenticationLevel level, Entity<User> user) {
			switch (level) {
				case PUBLIC: {
					return true;
				}
				
				default: {
					return false;
				}
			}
		}

		@Override
		public Entity<User> fromToken(String key, String token) {
			return null;
		}
		
	}
	
}
