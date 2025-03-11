/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.noti.v1.agent.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.NotificationServer;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.noti.v1.agent.dto.push.FcmNotificationDto;
import org.omnione.did.noti.v1.agent.dto.push.RequestSendPushReqDto;
import org.omnione.did.noti.v1.agent.dto.push.RequestSendPushResDto;
import org.omnione.did.noti.v1.common.service.query.NotificationServerQueryService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service class for sending push notifications.
 * Provides methods for sending push notifications.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotiPushService {
    private final NotiUserQueryService notiUserQueryService;
    private final NotiAppQueryService notiAppQueryService;
    private final Environment environment;
    private final NotificationServerQueryService notificationServerQueryService;

    /**
     * Initializes the Firebase application.
     * This method loads the Firebase credentials from a file and initializes the Firebase app
     * with those credentials if it's not already initialized. It skips the initialization
     * if the active Spring profile includes "sample".
     *
     * @throws IOException If an error occurs while reading the Firebase credentials file.
     */

    @PostConstruct
    public void init() {
        initializeFirebase();
    }

    public synchronized void initializeFirebase() {
        try {
            List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
            NotificationServer notificationServer = notificationServerQueryService.findNotificationServerOrNull(NotificationServerType.PUSH);
            if (notificationServer == null || notificationServer.getConfig() == null) {
                log.error("FCM configuration is missing in the database.");
                return;
            }

            if (!activeProfiles.contains("sample")) {
                String fcmConfigJson = notificationServer.getConfig();

                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(new ByteArrayInputStream(fcmConfigJson.getBytes(StandardCharsets.UTF_8)))
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(googleCredentials)
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    log.info("Firebase application has been initialized");
                }

                log.info("Firebase application is already initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase from database configuration", e);
        } catch (Exception e) {
            log.error("An unknown error occurred while initializing Firebase", e);
        }
    }

    /**
     * Sends a push notification to the specified devices.
     * This method retrieves push tokens based on the target DIDs, generates a push notification message,
     * and sends it using Firebase Cloud Messaging (FCM).
     *
     * @param requestSendPushReqDto The request DTO containing the push notification information.
     * @return The response DTO containing the results of the push notification, including the success and failure counts.
     * @throws OpenDidException If an error occurs while sending the push notification.
     */
    public RequestSendPushResDto requestSendPush(RequestSendPushReqDto requestSendPushReqDto) {
        try {
            log.debug("=== Starting requestSendPush ===");

            // Retrieve push token
            log.debug("\t--> Retrieving Push Token");
            List<String> pushTokenList = findPushTokenList(requestSendPushReqDto);

            // Generate push message
            log.debug("\t--> Generating Push Message");
            MulticastMessage multicastMessage = generateMulticastMessage(requestSendPushReqDto, pushTokenList);

            // Send push message
            log.debug("\t--> Send FCM");
            RequestSendPushResDto requestSendPushResDto = sendFcm(multicastMessage);

            log.debug("*** Finished requestSendPush ***");

            return requestSendPushResDto;
        } catch (OpenDidException e) {
            log.error("An error occurred while requesing send push", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while requesing send push", e);
            throw new OpenDidException(ErrorCode.FAILED_API_SEND_PUSH);
        }
    }

    /**
     * Finds the push tokens for the specified devices.
     * This method retrieves user IDs based on the target DIDs, then queries the associated App entities to extract the push tokens.
     * If no push tokens are found, an exception is thrown.
     *
     * @param requestSendPushReqDto The request DTO containing the target DIDs.
     * @return The list of push tokens.
     * @throws OpenDidException If no push tokens are found or an error occurs during the query.
     */
    private List<String> findPushTokenList(RequestSendPushReqDto requestSendPushReqDto) {
        List<Long> userIdList = notiUserQueryService.findIdsByDids(requestSendPushReqDto.getTargetDids());
        if (userIdList == null || userIdList.isEmpty()) {
            throw new OpenDidException(ErrorCode.USER_INFO_NOT_FOUND);
        }

        List<App> appList = notiAppQueryService.findByUserIds(userIdList);
        if (appList == null || appList.isEmpty()) {
            throw new OpenDidException(ErrorCode.APP_INFO_NOT_FOUND);
        }

        List<String> pushTokenList = appList.stream()
                .map(App::getPushToken)
                .filter(Objects::nonNull)
                .filter(token -> !token.isEmpty())
                .toList();

        if (pushTokenList.isEmpty()) {
            throw new OpenDidException(ErrorCode.PUSH_TOKEN_NOT_FOUND);
        }

        return pushTokenList;
    }

    /**
     * Sends the push notification using Firebase Cloud Messaging (FCM).
     * This method sends the prepared multicast message to the devices and returns the response from FCM.
     *
     * @param multicastMessage The multicast message to send.
     * @return The response DTO containing the results of the push notification, including the success and failure counts.
     * @throws OpenDidException If an error occurs while sending the push notification through FCM.
     */
    private RequestSendPushResDto sendFcm(MulticastMessage multicastMessage) {
        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);

            return RequestSendPushResDto.builder()
                    .successCount(batchResponse.getSuccessCount())
                    .failureCount(batchResponse.getFailureCount())
                    .build();
        } catch (FirebaseMessagingException e) {
            log.error("An error occurred while sending FCM", e);
            throw new OpenDidException(ErrorCode.FCM_SEND_FAILED);
        }
    }

    /**
     * Generates a multicast message for sending push notifications.
     * This method prepares the multicast message with the provided push tokens and notification data.
     *
     * @param requestSendPushReqDto The request DTO containing the push notification information.
     * @param pushTokenList The list of push tokens to which the notification will be sent.
     * @return The prepared multicast message ready to be sent via FCM.
     */
    private MulticastMessage generateMulticastMessage(RequestSendPushReqDto requestSendPushReqDto, List<String> pushTokenList) {
        return MulticastMessage.builder()
                .putAllData(requestSendPushReqDto.getData())
                .addAllTokens(pushTokenList)
                .setApnsConfig(generateApnsConfig(requestSendPushReqDto.getNotification()))
                .build();
    }

    /**
     * Generates an APNs (Apple Push Notification service) configuration for the push notification.
     * This method configures the APNs settings, such as the alert and sound, to be included in the push notification.
     *
     * @param notification The notification information to include in the APNs configuration.
     * @return The APNs configuration ready to be included in the multicast message.
     */
    private ApnsConfig generateApnsConfig(FcmNotificationDto notification) {
        ApsAlert apsAlert = ApsAlert.builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getBody())
                .build();

        Aps aps = Aps.builder()
                .setSound("default")
                .setMutableContent(true)
                .setAlert(apsAlert)
                .build();

        return ApnsConfig.builder()
                .setAps(aps)
                .build();
    }
}
