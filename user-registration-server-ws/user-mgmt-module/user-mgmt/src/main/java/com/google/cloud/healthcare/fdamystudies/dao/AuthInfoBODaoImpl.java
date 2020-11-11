/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.DeviceTokenAndDeviceType;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoRepository;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthInfoBODaoImpl implements AuthInfoBODao {

  private static final Logger logger = LoggerFactory.getLogger(AuthInfoBODaoImpl.class);

  @Autowired AuthInfoRepository authInfoRepository;

  @Override
  public AuthInfoEntity save(AuthInfoEntity authInfo) {
    logger.info("AuthInfoBODaoImpl save() - starts");
    AuthInfoEntity dbResponse = null;
    if (authInfo != null) {
      dbResponse = authInfoRepository.save(authInfo);
      logger.info("AuthInfoBODaoImpl save() - ends");
      return dbResponse;
    } else return null;
  }

  @Override
  public Map<String, JSONArray> getDeviceTokenOfAllUsers(List<AppEntity> appInfos) {
    logger.info("AuthInfoBODaoImpl.getDeviceTokenOfAllUsers()-Start");
    JSONArray androidJsonArray = null;
    JSONArray iosJsonArray = null;
    Map<String, JSONArray> deviceMap = new HashMap<>();
    List<String> appInfoIds =
        appInfos.stream().map(a -> a.getAppId()).distinct().collect(Collectors.toList());

    if (appInfoIds != null && !appInfoIds.isEmpty()) {
      List<DeviceTokenAndDeviceType> authInfos = authInfoRepository.findDevicesTokens(appInfoIds);
      if (authInfos != null && !authInfos.isEmpty()) {
        androidJsonArray = new JSONArray();
        iosJsonArray = new JSONArray();
        for (DeviceTokenAndDeviceType authInfo : authInfos) {
          String devicetoken = authInfo.getDeviceToken();
          String devicetype = authInfo.getDeviceType();
          if (devicetoken != null && devicetype != null) {
            if (devicetype.equalsIgnoreCase(AppConstants.DEVICE_ANDROID)) {
              androidJsonArray.put(devicetoken.trim());
            } else if (devicetype.equalsIgnoreCase(AppConstants.DEVICE_IOS)) {
              iosJsonArray.put(devicetoken.trim());
            } else {
              logger.error("Invalid Device Type");
            }
          }
        }
        deviceMap.put(AppConstants.DEVICE_ANDROID, androidJsonArray);
        deviceMap.put(AppConstants.DEVICE_IOS, iosJsonArray);
      }
    }

    logger.info("AuthInfoBODaoImpl.getDeviceTokenOfAllUsers()-end ");
    return deviceMap;
  }
}
