/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.DeviceTokenAndDeviceType;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface AuthInfoRepository extends JpaRepository<AuthInfoEntity, String> {

  @Query(
      "SELECT DISTINCT a.deviceToken AS deviceToken,a.deviceType AS deviceType  FROM UserAppDetailsEntity u,AuthInfoEntity a where u.userDetails = a.userDetails and u.app.appId in (?1) and a.remoteNotificationFlag=1 and "
          + "(a.deviceToken is not NULL and a.deviceToken != '' and a.deviceType is not NULL and a.deviceType != '') ")
  public List<DeviceTokenAndDeviceType> findDevicesTokens(List<String> appInfoIds);

  public Optional<AuthInfoEntity> findByUserDetails(UserDetailsEntity userDetails);
}