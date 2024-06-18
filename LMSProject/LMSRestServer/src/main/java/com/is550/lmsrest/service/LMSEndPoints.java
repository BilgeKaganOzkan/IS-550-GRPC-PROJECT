package com.is550.lmsrest.service;

import com.is550.lmsrest.database.LMSDatabase;
import com.is550.lmsrest.exceptions.InvalidParametersException;
import com.is550.lmsrest.variables.*;
import com.lms.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


@RestController
public class LMSEndPoints {
    private final LMSDatabase lmsDatabase;

    @Autowired
    public LMSEndPoints(LMSDatabase lmsDatabase) {
        this.lmsDatabase = lmsDatabase;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<UserLoginInfosRest>> login(@RequestBody LoginRequestRest login) {
        UserLoginInfosRest userInfo = lmsDatabase.findUserId(login.getEmail(), login.getPassword());
        EntityModel<UserLoginInfosRest> resource = EntityModel.of(userInfo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).login(login)).withSelfRel());

        if (UserTypeRest.USER.equals(userInfo.getUserType())) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(null, userInfo.getUserId())).withRel("get-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(null, null, userInfo.getUserId())).withRel("search-books"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserBorrowingInfos(lmsDatabase.translateUserIdToStudentId(userInfo.getUserId()), userInfo.getUserId())).withRel("borrowing-info"));
            resource.add(WebMvcLinkBuilder.linkTo((WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateContactInfo(null, userInfo.getUserId()))).withRel("update-contact-info"));
            resource.add(WebMvcLinkBuilder.linkTo((WebMvcLinkBuilder.methodOn(LMSEndPoints.class).changePassword(null, userInfo.getUserId()))).withRel("change-password"));
        } else if (UserTypeRest.LIBRARIAN.equals(userInfo.getUserType())) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(null, userInfo.getUserId())).withRel("get-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(null, null, userInfo.getUserId())).withRel("search-books"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserBorrowingInfos(null, userInfo.getUserId())).withRel("borrowing-info"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBorrowBook(null, userInfo.getUserId())).withRel("borrow-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addReturnBook(null, userInfo.getUserId())).withRel("return-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBook(null, userInfo.getUserId())).withRel("add-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(null, userInfo.getUserId())).withRel("delete-book"));
            resource.add(WebMvcLinkBuilder.linkTo((WebMvcLinkBuilder.methodOn(LMSEndPoints.class).changePassword(null, userInfo.getUserId()))).withRel("change-password"));
        } else if(UserTypeRest.ADMIN.equals(userInfo.getUserType())){
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addUser(null, userInfo.getUserId())).withRel("add-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteUser(null, userInfo.getUserId())).withRel("delete-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateUserInfo(null, userInfo.getUserId())).withRel("update-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo(null, userInfo.getUserId())).withRel("get-user"));
            resource.add(WebMvcLinkBuilder.linkTo((WebMvcLinkBuilder.methodOn(LMSEndPoints.class).changePassword(null, userInfo.getUserId()))).withRel("change-password"));
        }

        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/user/info/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<UserBorrowingInfosRest>>> getUserBorrowingInfos(@PathVariable Long studentId, @RequestHeader("Login-ID") Long loginId) {
        List<UserBorrowingInfosRest> borrowingInfos = lmsDatabase.findUserInfo(loginId, studentId);
        List<EntityModel<UserBorrowingInfosRest>> resourceList = new ArrayList<>();
        for (UserBorrowingInfosRest borrowingInfo : borrowingInfos) {
            EntityModel<UserBorrowingInfosRest> resource = EntityModel.of(borrowingInfo);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(borrowingInfo.getBook().getId(), loginId)).withRel("get-book"));
            if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBorrowBook(null, loginId)).withRel("borrow-book"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addReturnBook(null, loginId)).withRel("return-book"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(borrowingInfo.getBook().getId(), loginId)).withRel("delete-book"));
            }

            resourceList.add(resource);
        }

        CollectionModel<EntityModel<UserBorrowingInfosRest>> resources = CollectionModel.of(resourceList);

        return ResponseEntity.ok(resources);
    }

    @GetMapping(value = "/book/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<BookRest>> getBookById(@PathVariable Long bookId, @RequestHeader("Login-ID") Long loginId) {
        BookRest book = lmsDatabase.findBookById(loginId, bookId);
        EntityModel<BookRest> resource = EntityModel.of(book);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(bookId, loginId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(book.getName(), book.getAuthor(), loginId)).withRel("search-books"));
        if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(book.getId(), loginId)).withRel("delete-book"));
        }
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/book/{name}/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<BookRest>>> searchBooks(
            @PathVariable(required = false) String name,
            @PathVariable(required = false) String author,
            @RequestHeader("Login-ID") Long loginId) {
        try {
            name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            author = URLDecoder.decode(author, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            ;
        }

        List<BookRest> books = null;
        if (!Objects.equals(name, "null") && Objects.equals(author, "null")) {
            books = lmsDatabase.findBooksByName(loginId, name);
        } else if (!Objects.equals(author, "null") && Objects.equals(name, "null")) {
            books = lmsDatabase.findBooksByAuthor(loginId, author);
        } else if (!Objects.equals(name, "null") && !Objects.equals(author, "null")) {
            List<BookRest> name_books = lmsDatabase.findBooksByName(loginId, name);
            List<BookRest> author_books = lmsDatabase.findBooksByAuthor(loginId, author);
            name_books.retainAll(author_books);
            books = name_books;
        } else {
            throw new InvalidParametersException("Invalid parameters");
        }

        List<EntityModel<BookRest>> bookResources = new ArrayList<>();

        for(BookRest book : books){
            EntityModel<BookRest> resource = EntityModel.of(book);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(book.getId(), loginId)).withRel("get-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(name, author, loginId)).withSelfRel());
            if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(book.getId(), loginId)).withRel("delete-book"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBorrowBook(null, loginId)).withRel("borrow-book"));
            }

            bookResources.add(resource);
        }
        CollectionModel<EntityModel<BookRest>> resources = CollectionModel.of(bookResources);
        return ResponseEntity.ok(resources);
    }

    @PostMapping(value = "/book/borrow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> addBorrowBook(@RequestBody AddBorrowBookRequestRest borrowBook, @RequestHeader("Login-ID") Long loginId) {
        ReturnTypeRest result = lmsDatabase.addBorrowBook(loginId,
                borrowBook.getBorrowBook().getStudentId(),
                borrowBook.getBorrowBook().getBookId(),
                borrowBook.getBorrowBook().getBorrowingTime(),
                borrowBook.getBorrowBook().getDueDate());

        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        returnTypeResponse.setReturnVal(result);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBorrowBook(null, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addReturnBook(null, loginId)).withRel("return-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(borrowBook.getBorrowBook().getBookId(), loginId)).withRel("delete-book"));
        }
        return ResponseEntity.ok(resource);
    }

    @PutMapping(value = "/book/return", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> addReturnBook(@RequestBody AddReturnBookRequestRest returnBook, @RequestHeader("Login-ID") Long loginId) {
        ReturnTypeRest result = lmsDatabase.addReturnBook(
                loginId,
                returnBook.getReturnBook().getStudentId(),
                returnBook.getReturnBook().getBookId(),
                returnBook.getReturnBook().getReturningTime()
        );
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        returnTypeResponse.setReturnVal(result);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addReturnBook(null, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(returnBook.getReturnBook().getBookId(), loginId)).withRel("delete-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getBookById(returnBook.getReturnBook().getBookId(), loginId)).withRel("get-book"));
        }
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/book/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> addBook(@RequestBody AddBookRequestRest request, @RequestHeader("Login-ID") Long loginId) {
        Long bookId = lmsDatabase.addBook(loginId, request.getAddBook().getName(), request.getAddBook().getType(), request.getAddBook().getAuthor(), request.getAddBook().getLocation());
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        returnTypeResponse.setReturnLongVal(bookId);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addBook(null, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(bookId, loginId)).withRel("delete-book"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(null, null, loginId)).withRel("search-books"));
        }
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping(value = "/book/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> deleteBook(@PathVariable Long bookId, @RequestHeader("Login-ID") Long loginId) {
        ReturnTypeRest result = lmsDatabase.deleteBook(loginId, bookId);
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        returnTypeResponse.setReturnVal(result);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsLibrarian(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteBook(bookId, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).searchBooks(null, null, loginId)).withRel("search-books"));
        }
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/admin/adduser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> addUser(@RequestBody AddUserRequestRest request, @RequestHeader("Login-ID") Long loginId) {
        Function<UserTypeRest, UserType> userTypeToUserTypeGrpc = type -> {
            switch (type) {
                case USER:
                    return UserType.USER;
                case LIBRARIAN:
                    return UserType.LIBRARIAN;
                case ADMIN:
                    return UserType.ADMIN;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + type);
            }
        };

        UserType userTypeGrpc = userTypeToUserTypeGrpc.apply(request.getUser().getType());

        User grpcUser = User.newBuilder()
                .setStudentID(request.getUser().getStudentID())
                .setName(request.getUser().getName())
                .setSurname(request.getUser().getSurname())
                .setEmail(request.getUser().getEmail())
                .setPassword(request.getUser().getPassword())
                .setTelNumber(request.getUser().getTelNumber())
                .setLocation(request.getUser().getLocation())
                .setDepartment(request.getUser().getDepartment())
                .setType(userTypeGrpc)
                .build();

        AddUserResponse response = lmsDatabase.grpcClient.addUser(Math.toIntExact(loginId), grpcUser);
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        returnTypeResponse.setReturnLongVal((long) response.getUserID());
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsAdmin(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addUser(request, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteUser((long) response.getUserID(), loginId)).withRel("delete-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo((long) response.getUserID(), loginId)).withRel("get-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateUserInfo(null, loginId)).withRel("update-user"));
        }
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping(value = "/admin/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> deleteUser(@PathVariable Long userId, @RequestHeader("Login-ID") Long loginId) {
        GenericResponse response = lmsDatabase.grpcClient.deleteUser(Math.toIntExact(loginId), Math.toIntExact(userId));
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        ReturnTypeRest returnVal = null;
        returnVal = returnVal.fromValue(response.getMessage());
        returnTypeResponse.setReturnVal(returnVal);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsAdmin(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteUser(userId, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).addUser(null, loginId)).withRel("add-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo(userId, loginId)).withRel("get-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateUserInfo(null, loginId)).withRel("update-user"));
        }
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/admin/changePassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> changePassword(@RequestBody ChangePasswordRequestRest request, @RequestHeader("Login-ID") Long loginId) {
        GenericResponse response = lmsDatabase.grpcClient.changePassword(Math.toIntExact(loginId), request.getOldPassword(), request.getNewPassword());
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        ReturnTypeRest returnVal = null;
        returnVal = returnVal.fromValue(response.getMessage());
        returnTypeResponse.setReturnVal(returnVal);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).changePassword(null, loginId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).login(null)).withRel("login"));
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/admin/updateContactInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> updateContactInfo(@RequestBody UpdateContactInfoRequestRest request, @RequestHeader("Login-ID") Long loginId) {
        GenericResponse response = lmsDatabase.grpcClient.updateContactInfo(Math.toIntExact(loginId), request.getNewTelNumber(), request.getNewLocation());
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        ReturnTypeRest returnVal = null;
        returnVal = returnVal.fromValue(response.getMessage());
        returnTypeResponse.setReturnVal(returnVal);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateContactInfo(null, loginId)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo(loginId, loginId)).withRel("get-user"));
        return ResponseEntity.ok(resource);
    }

    @PostMapping(value = "/admin/updateUserInfo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ReturnTypeResponseRest>> updateUserInfo(@RequestBody UpdateUserInfoRequestRest request, @RequestHeader("Login-ID") Long loginId) {
        Function<UserTypeRest, UserType> userTypeToUserTypeGrpc = type -> {
            switch (type) {
                case USER:
                    return UserType.USER;
                case LIBRARIAN:
                    return UserType.LIBRARIAN;
                case ADMIN:
                    return UserType.ADMIN;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + type);
            }
        };

        UserType userTypeGrpc = userTypeToUserTypeGrpc.apply(request.getUser().getType());

        User grpcUser = User.newBuilder()
                .setStudentID(request.getUser().getStudentID())
                .setName(request.getUser().getName())
                .setSurname(request.getUser().getSurname())
                .setEmail(request.getUser().getEmail())
                .setPassword(request.getUser().getPassword())
                .setTelNumber(request.getUser().getTelNumber())
                .setLocation(request.getUser().getLocation())
                .setDepartment(request.getUser().getDepartment())
                .setType(userTypeGrpc)
                .build();

        GenericResponse response = lmsDatabase.grpcClient.updateUserInfo(Math.toIntExact(loginId), Math.toIntExact(request.getUserID()), grpcUser);
        ReturnTypeResponseRest returnTypeResponse = new ReturnTypeResponseRest();
        ReturnTypeRest returnVal = null;
        returnVal = returnVal.fromValue(response.getMessage());
        returnTypeResponse.setReturnVal(returnVal);
        EntityModel<ReturnTypeResponseRest> resource = EntityModel.of(returnTypeResponse);
        if (lmsDatabase.checkLoginIdIsAdmin(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateUserInfo(null, loginId)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteUser((long) request.getUserID(), loginId)).withRel("delete-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo((long) request.getUserID(), loginId)).withRel("get-user"));
        }
        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/admin/getUserInfo/{userID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<GetUserInfoResponseRest>> getUserInfo(@PathVariable Long userID, @RequestHeader("Login-ID") Long loginId) {
        GetUserInfoResponse response = lmsDatabase.grpcClient.getUserInfo(Math.toIntExact(loginId), Math.toIntExact(userID));

        Function<UserType, UserTypeRest> userTypeToUserTypeRest = type -> {
            switch (type) {
                case USER:
                    return UserTypeRest.USER;
                case LIBRARIAN:
                    return UserTypeRest.LIBRARIAN;
                case ADMIN:
                    return UserTypeRest.ADMIN;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + type);
            }
        };

        UserTypeRest userTypeRest = userTypeToUserTypeRest.apply(response.getUser().getType());

        UserInfoRest userRest = new UserInfoRest();
        userRest.setStudentID(response.getUser().getStudentID());
        userRest.setName(response.getUser().getName());
        userRest.setSurname(response.getUser().getSurname());
        userRest.setTelNumber(response.getUser().getTelNumber());
        userRest.setLocation(response.getUser().getLocation());
        userRest.setDepartment(response.getUser().getDepartment());
        userRest.setType(userTypeRest);

        GetUserInfoResponseRest restResponse = new GetUserInfoResponseRest();
        restResponse.setUser(userRest);

        EntityModel<GetUserInfoResponseRest> resource = EntityModel.of(restResponse);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).getUserInfo(userID, loginId)).withSelfRel());
        if (lmsDatabase.checkLoginIdIsAdmin(loginId)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).deleteUser(userID, loginId)).withRel("delete-user"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateUserInfo(null, loginId)).withRel("update-user"));
        }else {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LMSEndPoints.class).updateContactInfo(null, loginId)).withRel("update-contact-info"));
        }
        return ResponseEntity.ok(resource);
    }
}