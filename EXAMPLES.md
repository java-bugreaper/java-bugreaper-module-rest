# Examples of usage

#### Simple use in tests

```java
import io.bugreaper.modules.api.Api;

public class MyTests {
    Api api = new Api("http://localhost", 8080);

    //...use base methods from Api as is (api.XXX)
}  
```

#### Create object with extend:

```java
import io.bugreaper.modules.api.Api;

public class ApiHelper extends Api {

    public MinioSetup() {
        super("http://localhost", 8080);
    }

    //...use base methods from Api as is (api.XXX)
}  
```

#### Create object with wrapper:

```java
import io.bugreaper.modules.api.Api;


public class ApiHelper {

    private final Api apiHelper;
    private static ApiHelper instance;

    private ApiHelper() {
        this.apiHelper = new Api(
                "http://localhost",
                8080);
    }

    public static ApiHelper getInstance() {
        if (instance == null) {
            instance = new ApiHelper();
        }

        return instance;
    }

    @Step("(API) GET employer")  //if you need wrapper step
    public AssertableResponse getEmployer(int id) {
        return apiHelper
                .sendGet("/employees/" + id);
    }

    //...other methods with your wrappers
}

class GetTests {

    @Test
    @Description("""
            Get existing employer""")
    void getExistingEmployer() {

        api.getEmployer(1)
                .seeResponseCodeIs(200)
                .seeResponseExactlyMatchJson("""
                        {
                            "id": 1,
                            "firstName": "Alex",
                            "lastName": "Userov",
                            "emailId": "alex@com.com"
                        }
                        """);
    }
}


```

#### Create object with base methods & Singleton:

```java
import io.bugreaper.modules.api.Api;

public class ApiHelper {

    private static ApiHelper instance;
    private final Api api;


    public ApiHelper() {
        this.api = new Api("http://localhost", 8080);
    }

    public static ApiHelper getInstance() {
        if (instance == null) {
            instance = new ApiHelper();
        }

        return instance;
    }

    //..use base methods from Minio as is
}  
```

#### Setup example:

```java
//if used Singleton
private final Api api = ApiHelper.getInstance();

//if used like object
private final Api api = new ApiHelper();
```

#### Test examples with upload & download:

```java
private final Api api = new ApiHelper();

@Test
void test() {

    api.sendGet("/employees/" + id)
            .seeResponseCodeIs(200)
            .seeResponseBodyFieldMatch("name", is("Jonn"))
            .seeResponseBodyFieldMatch("age", is(5))
            .seeResponseBodyFieldMatch("name2", is("Jonn again"));

}
```

## Real examples here:

- ### [Report-api](https://bug-reaper.gitlab.io/java-bugreaper-sandbox/#behaviors)
- ### [Tests](https://gitlab.com/bug-reaper/java-bugreaper-sandbox/-/tree/main/java-test-part1)