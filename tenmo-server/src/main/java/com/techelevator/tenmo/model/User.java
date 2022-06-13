package com.techelevator.tenmo.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity(name="tenmo_user")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @NotNull
   private Long userId;
   @NotEmpty
   private String username;
   @NotEmpty
   @Column(name = "password_hash", length = 200, nullable = false)
   private String password;

   @Transient
   private boolean activated;
   @Transient
   private Set<Authority> authorities = new HashSet<>();

   public User() { }

   public User(Long userId, String username, String password) {
      this.userId = userId;
      this.username = username;
      this.password = password;
   }

   public User(Long userId, String username, String password, String authorities) {
      this.userId = userId;
      this.username = username;
      this.password = password;
      this.activated = true;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isActivated() {
      return activated;
   }

   public void setActivated(boolean activated) {
      this.activated = activated;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }

   public void setAuthorities(String authorities) {
      String[] roles = authorities.split(",");
      for(String role : roles) {
         this.authorities.add(new Authority("ROLE_" + role));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return userId == user.userId &&
              activated == user.activated &&
              Objects.equals(username, user.username) &&
              Objects.equals(password, user.password) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(userId, username, password, activated, authorities);
   }

//   @Override
//   public String toString() {
//      return "User{" +
//              "id=" + id +
//              ", username='" + username + '\'' +
//              ", activated=" + activated +
//              ", authorities=" + authorities +
//              '}';
//   }


   @Override
   public String toString() {
      return "User{" +
              "id=" + userId +
              ", username='" + username + '\'' +
              '}';
   }
}
