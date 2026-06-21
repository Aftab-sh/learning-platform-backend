package com.learningplatform.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPrincipal implements UserDetails
{

	private User  user;
	private final Role role;
	
	 public UserPrincipal(User user ,Role role)
	 {
	 this.user=user;
	this.role = role;
	 }

	 
	 //so we are adding the list of role and permissions inside this particular authorities
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		//no SimpleGrantedAuthority have a list of role for each role 
		//whatever permissions we have  so all permission will be add into authorites
		Set<SimpleGrantedAuthority> authorities=new HashSet<>();
		//we need to add simpleGrantedAuthority for each roles
		authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
		//so now we are added list of roles and permission is added 
		Set<SimpleGrantedAuthority> permissionAuthorities=role.getPermissions().stream().
		
				//each pemition converted into simple granted Auth

						map(permissions -> new SimpleGrantedAuthority(permissions.name()))
				.collect(Collectors.toSet());
		authorities.addAll(permissionAuthorities);
		return authorities;
		
	}

	@Override
	public String getPassword()
	{
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}
	
	
	
}