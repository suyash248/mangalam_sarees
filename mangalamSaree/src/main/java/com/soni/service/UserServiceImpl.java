package com.soni.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soni.model.User;
import com.soni.model.UserAuthority;
import com.soni.service.AuthServiceImpl.SecureUser;

@Repository
@Transactional(propagation=Propagation.REQUIRED, timeout=300)
public class UserServiceImpl extends AbstractServiceImpl implements UserService {
	
	public User getUserDetails(String userName){
		Session session = getSession();
		User user;
		String sql = "select usr from User usr "
				+ " where usr.userName=:userName ";
		Query query = session.createQuery(sql);
		query.setParameter("userName", userName);
		user = (User) query.uniqueResult();
		return user;
	}
	
	public User createOpenIdUser(String userName){
		User user = new User();
		user.setActive(true);
		user.setUserName(userName);
		user.setUserPass("");
		getSession().save(user);
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserAuthority> getIndividualUserAuthorities(String userName){
		Session session = getSession();
		List<UserAuthority> userAuthorities = new ArrayList<UserAuthority>();
		String sql = "select distinct ua from UserAuthority ua "
				+ " left join fetch ua.user usr "
				+ " left join fetch ua.authority auth "
				+ " where usr.userName=:userName";
		Query query = session.createQuery(sql);
		query.setParameter("userName", userName);
		userAuthorities = query.list();
		return userAuthorities;
	}
	
	public boolean changePassword(String currentPassword, String newPassword) {
		SecureUser secureUser = (SecureUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(currentPassword.equals(secureUser.getUser().getUserPass())){
			User user = secureUser.getUser();
			user.setUserPass(newPassword);
			getSession().saveOrUpdate(user);
			return true;
		}
		return false;
	}
	
}
