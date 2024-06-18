package com.lms.grpcServer;

import com.lms.grpc.*;
import io.grpc.stub.StreamObserver;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final LMSDatabase lmsDatabase;

    public AdminServiceImpl() {
        String databaseUrl = "jdbc:sqlite:users.db";
        this.lmsDatabase = new LMSDatabase(databaseUrl);
    }

    @Override
    public void addUser(AddUserRequest request, StreamObserver<AddUserResponse> responseObserver) {
        int loginID = request.getLoginID();
        User user = request.getUser();

        try {
            AddUserResponse response = lmsDatabase.addUser(loginID, user.getStudentID(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getTelNumber(), user.getLocation(), user.getDepartment(), user.getType());
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<GenericResponse> responseObserver) {
        int loginID = request.getLoginID();
        int userID = request.getUserID();

        try {
            lmsDatabase.deleteUser(loginID, userID);
            GenericResponse response = GenericResponse.newBuilder()
                    .setMessage("ok")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<GenericResponse> responseObserver) {
        int loginID = request.getLoginID();
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();

        try {
            lmsDatabase.changePassword(loginID, oldPassword, newPassword);
            GenericResponse response = GenericResponse.newBuilder()
                    .setMessage("ok")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void updateContactInfo(UpdateContactInfoRequest request, StreamObserver<GenericResponse> responseObserver) {
        int loginID = request.getLoginID();
        String newTelNumber = request.getNewTelNumber();
        String newLocation = request.getNewLocation();

        try {
            lmsDatabase.updateContactInfo(loginID, newTelNumber, newLocation);
            GenericResponse response = GenericResponse.newBuilder()
                    .setMessage("ok")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void updateUserInfo(UpdateUserInfoRequest request, StreamObserver<GenericResponse> responseObserver) {
        int loginID = request.getLoginID();
        int userID = request.getUserID();
        User user = request.getUser();

        try {
            lmsDatabase.updateUserInfo(loginID, userID, user.getStudentID(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getTelNumber(), user.getLocation(), user.getDepartment(), user.getType());
            GenericResponse response = GenericResponse.newBuilder()
                    .setMessage("ok")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getUserInfo(GetUserInfoRequest request, StreamObserver<GetUserInfoResponse> responseObserver) {
        try {
            if(!lmsDatabase.isAdmin(request.getLoginID()) && request.getLoginID() != request.getUserID()){
                throw new Exception("You are not authorized to access this user");
            }
            User user = lmsDatabase.getUserInfo(request.getUserID());
            GetUserInfoResponse response = GetUserInfoResponse.newBuilder().setUser(user).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void isLibrarian(IsLibrarianRequest request, StreamObserver<IsLibrarianResponse> responseObserver) {
        try {
            boolean isLibrarian = lmsDatabase.isLibrarian(request.getLoginID());
            IsLibrarianResponse response = IsLibrarianResponse.newBuilder().setIsLibrarian(isLibrarian).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void isAdmin(IsAdminRequest request, StreamObserver<IsAdminResponse> responseObserver) {
        try {
            boolean isAdmin = lmsDatabase.isAdmin(request.getLoginID());
            IsAdminResponse response = IsAdminResponse.newBuilder().setIsAdmin(isAdmin).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void getLoginID(GetLoginIDRequest request, StreamObserver<GetLoginIDResponse> responseObserver) {
        try {
            GetLoginIDResponse response = lmsDatabase.getLoginID(request.getEmail(), request.getPassword());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void isUserExist(IsUserExistRequest request, StreamObserver<IsUserExistResponse> responseObserver) {
        try {
            boolean isUserExist = lmsDatabase.userExistsID(request.getLoginID());
            IsUserExistResponse response = IsUserExistResponse.newBuilder().setIsExist(isUserExist).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void translateStudentIdToUserId(TranslateStudentIdToUserIdRequest request, StreamObserver<TranslateStudentIdToUserIdResponse> responseObserver) {
        try {
            long userId = lmsDatabase.translateStudentIdToUserId(request.getStudentID());
            TranslateStudentIdToUserIdResponse response = TranslateStudentIdToUserIdResponse.newBuilder().setUserID((int) userId).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }

    @Override
    public void translateUserIdToStudentId(TranslateUserIdToStudentIdRequest request, StreamObserver<TranslateUserIdToStudentIdResponse> responseObserver) {
        try {
            long studentId = lmsDatabase.translateUserIdToStudentId(request.getUserID());
            TranslateUserIdToStudentIdResponse response = TranslateUserIdToStudentIdResponse.newBuilder().setStudentID((int) studentId).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GRPCErrorHandler.handleException(e));
        }
    }
}
