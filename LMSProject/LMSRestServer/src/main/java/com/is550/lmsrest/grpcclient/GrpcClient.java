package com.is550.lmsrest.grpcclient;

import com.lms.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

    private final AdminServiceGrpc.AdminServiceBlockingStub blockingStub;

    public GrpcClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = AdminServiceGrpc.newBlockingStub(channel);
    }

    public boolean isLibrarian(Long loginID) {
        IsLibrarianRequest request = IsLibrarianRequest.newBuilder()
                .setLoginID(Math.toIntExact(loginID))
                .build();
        IsLibrarianResponse response = blockingStub.isLibrarian(request);
        return response.getIsLibrarian();
    }

    public boolean isAdmin(Long loginID) {
         IsAdminRequest request = IsAdminRequest.newBuilder()
                .setLoginID(Math.toIntExact(loginID))
                .build();
        IsAdminResponse response = blockingStub.isAdmin(request);
        return response.getIsAdmin();
    }

    public boolean isUserExist(Long loginID) {
        IsUserExistRequest request = IsUserExistRequest.newBuilder()
                .setLoginID(Math.toIntExact(loginID))
                .build();
        IsUserExistResponse response = blockingStub.isUserExist(request);
        return response.getIsExist();
    }

    public GetLoginIDResponse getLoginID(String email, String password) {
        GetLoginIDRequest request = GetLoginIDRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();
        GetLoginIDResponse response = blockingStub.getLoginID(request);
        return response;
    }

    public AddUserResponse addUser(int loginID, User user){
        AddUserRequest request = AddUserRequest.newBuilder()
                .setLoginID(loginID)
                .setUser(user)
                .build();
        AddUserResponse response = blockingStub.addUser(request);
        return response;
    }

    public GenericResponse deleteUser(int loginID, int userId){
        DeleteUserRequest request = DeleteUserRequest.newBuilder()
                .setLoginID(loginID)
                .setUserID(userId)
                .build();
        GenericResponse response = blockingStub.deleteUser(request);
        return response;
    }

    public GenericResponse changePassword(int loginID, String oldPassword, String newPassword){
        ChangePasswordRequest request = ChangePasswordRequest.newBuilder()
                .setLoginID(loginID)
                .setOldPassword(oldPassword)
                .setNewPassword(newPassword)
                .build();
        GenericResponse response = blockingStub.changePassword(request);
        return response;
    }

    public GenericResponse updateContactInfo(int loginID, String newTelNumber, String newLocation){
        UpdateContactInfoRequest request = UpdateContactInfoRequest.newBuilder()
                .setLoginID(loginID)
                .setNewLocation(newLocation)
                .setNewTelNumber(newTelNumber)
                .build();
        GenericResponse response = blockingStub.updateContactInfo(request);
        return response;
    }

    public GenericResponse updateUserInfo(int loginID, int userID, User user){
        UpdateUserInfoRequest request = UpdateUserInfoRequest.newBuilder()
                .setLoginID(loginID)
                .setUserID(userID)
                .setUser(user)
                .build();
        GenericResponse response = blockingStub.updateUserInfo(request);
        return response;
    }

    public GetUserInfoResponse getUserInfo(int loginID, int userID){
        GetUserInfoRequest request = GetUserInfoRequest.newBuilder()
                .setLoginID(loginID)
                .setUserID(userID)
                .build();
        GetUserInfoResponse response = blockingStub.getUserInfo(request);
        return response;
    }

    public TranslateUserIdToStudentIdResponse translateUserIdToStudentId(int userID){
        TranslateUserIdToStudentIdRequest request = TranslateUserIdToStudentIdRequest.newBuilder()
                .setUserID(userID)
                .build();
        TranslateUserIdToStudentIdResponse response = blockingStub.translateUserIdToStudentId(request);
        return response;
    }

    public TranslateStudentIdToUserIdResponse translateStudentIdToUserId(int studentID){
        TranslateStudentIdToUserIdRequest request = TranslateStudentIdToUserIdRequest.newBuilder()
                .setStudentID(studentID)
                .build();
        TranslateStudentIdToUserIdResponse response = blockingStub.translateStudentIdToUserId(request);
        return response;
    }
}