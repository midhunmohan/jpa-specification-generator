# Search and Filters using JPA

* [What is this project?](#what-is-this-project)
* [Requirements](#requirements)
* [Using the project](#using-mapstruct)
 * [Maven](#maven)
* [Documentation and getting help](#documentation-and-getting-help)
* [Building from Source](#building-from-source)
* [Links](#links)
* [Licensing](#licensing)

## What is this project?

This Project is a Java [annotation processor](http://docs.oracle.com/javase/6/docs/technotes/guides/apt/index.html) for the generation JPA Specifications for an Entity. It saves you from writing different filter criterias, search over certain fields which are a tedious and error-prone task. The generator comes with sensible defaults and many built-in type conversions.

project offers the following advantages:

* **JPA Specifications** by using plain method invocations instead of reflection
* **Support for major data types**. Only objects and attributes mapping to each other can be mapped, so there's no accidental mapping of an order entity into a customer DTO, etc.
* **Generate Filter DTO if needed**—no runtime dependencies
* **Highly Configurable** at build time if:
  * mappings are incomplete (not all target properties are mapped)
  * mappings are incorrect (cannot find a proper mapping method or type conversion)
* **Easily debuggable code** (or editable by hand—e.g. in case of a bug in the generator)

The following is an Example provided for the better understanding

```java
@Entity
@Table(name = "item")
@Data
@SearchAndFilter(name="mysearchandfilter", exclude={"id"}, searchOver = {"name", "code"} )
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String code;

    @NotNull
    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @NotNull
    @JsonIgnore
    @Column(name = "created_by")
    private Long createdBy;
}
```
At compile time it will generate an implementation of this interface. The generated implementation uses criteria builders and consolidate as a JPA specification for the particular entity, i.e. no reflection is involved.
```java
public class MysearchandfilterSpecification {
  public static Specification<Item> withNameEquals(String name) {
    return (root, query, cb) -> name == null ? null: cb.equal(root.get("name"),name);
  }

  public static Specification<Item> withCodeEquals(String code) {
    return (root, query, cb) -> code == null ? null: cb.equal(root.get("code"),code);
  }

  public static Specification<Item> withCreatedDateEquals(ZonedDateTime createdDate) {
    return (root, query, cb) -> createdDate == null ? null: cb.equal(root.get("createdDate"),createdDate);
  }

  public static Specification<Item> withCreatedByEquals(Long createdBy) {
    return (root, query, cb) -> createdBy == null ? null: cb.equal(root.get("createdBy"),createdBy);
  }

  public static Specification<Item> withNameIn(List<String> name) {
    return (root, query, cb) -> ( name == null || name.isEmpty()) ? null: root.get("name").in(name);
  }

  public static Specification<Item> withCodeIn(List<String> code) {
    return (root, query, cb) -> ( code == null || code.isEmpty()) ? null: root.get("code").in(code);
  }

  public static Specification<Item> withCreatedByIn(List<Long> createdBy) {
    return (root, query, cb) -> ( createdBy == null || createdBy.isEmpty()) ? null: root.get("createdBy").in(createdBy);
  }

  public static Specification<Item> withNameLike(String name) {
    return (root, query, cb) -> ( name == null || name.isEmpty()) ? null: cb.like(root.get("name"), "%"+ name + "%");
  }

  public static Specification<Item> withCodeLike(String code) {
    return (root, query, cb) -> ( code == null || code.isEmpty()) ? null: cb.like(root.get("code"), "%"+ code + "%");
  }

  public static Specification<Item> withCreatedDateGreaterThanOrEqual(ZonedDateTime createdDate) {
    return (root, query, cb) -> ( createdDate == null ) ? null: cb.greaterThanOrEqualTo(root.get("createdDate"), createdDate);
  }

  public static Specification<Item> withCreatedDateGreaterThan(ZonedDateTime createdDate) {
    return (root, query, cb) -> ( createdDate == null) ? null: cb.greaterThan(root.get("createdDate"), createdDate);
  }

  public static Specification<Item> withCreatedDateLessThanOrEqual(ZonedDateTime createdDate) {
    return (root, query, cb) -> ( createdDate == null ) ? null: cb.lessThanOrEqualTo(root.get("createdDate"), createdDate);
  }

  public static Specification<Item> withCreatedDateLessThan(ZonedDateTime createdDate) {
    return (root, query, cb) -> ( createdDate == null ) ? null: cb.lessThan(root.get("createdDate"), createdDate);
  }

  public static Specification<Item> withCreatedByGreaterThanOrEqual(Long createdBy) {
    return (root, query, cb) -> ( createdBy == null ) ? null: cb.greaterThanOrEqualTo(root.get("createdBy"), createdBy);
  }

  public static Specification<Item> withCreatedByGreaterThan(Long createdBy) {
    return (root, query, cb) -> ( createdBy == null) ? null: cb.greaterThan(root.get("createdBy"), createdBy);
  }

  public static Specification<Item> withCreatedByLessThanOrEqual(Long createdBy) {
    return (root, query, cb) -> ( createdBy == null ) ? null: cb.lessThanOrEqualTo(root.get("createdBy"), createdBy);
  }

  public static Specification<Item> withCreatedByLessThan(Long createdBy) {
    return (root, query, cb) -> ( createdBy == null ) ? null: cb.lessThan(root.get("createdBy"), createdBy);
  }

  public static Specification<Item> returnTrue() {
    return (root, query, cb) -> cb.isTrue(cb.literal(true));
  }
}
```

```java
public class MysearchandfilterDTO {
  private long serialVersionUID;

  private String name;

  private String code;

  private ZonedDateTime createdDate;

  private Long createdBy;

  private List<String> nameList;

  private List<String> codeList;

  private List<Long> createdByList;

  public long getSerialVersionUID() {
    return this.serialVersionUID;
  }

  public void setSerialVersionUID(long serialVersionUID) {
    this.serialVersionUID=serialVersionUID;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name=name;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code=code;
  }

  public ZonedDateTime getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(ZonedDateTime createdDate) {
    this.createdDate=createdDate;
  }

  public Long getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy=createdBy;
  }

  public List<String> getNameList() {
    return this.nameList;
  }

  public void setNameList(List<String> nameList) {
    this.nameList=nameList;
  }

  public List<String> getCodeList() {
    return this.codeList;
  }

  public void setCodeList(List<String> codeList) {
    this.codeList=codeList;
  }

  public List<Long> getCreatedByList() {
    return this.createdByList;
  }

  public void setCreatedByList(List<Long> createdByList) {
    this.createdByList=createdByList;
  }
}
```
## How to use
```java
@Service
public class ItemService{
@Autowired
private MysearchandfilterSearchAndFilterRepository itemRepository;

public Page<Item> getAllItems(Pageable pageable, MySearchAndFilterDTO itemFilterDTO){
    return itemRepository
        .findAll(Specification.where(MysearchandfilterSpecification.withNameEquals(itemFilterDTO.getName())), pageable);
}
}
```



## Requirements

Requires Java 1.8 or later.


### Maven

For Maven-based projects, add the following to your POM file in order to use (the dependencies are not yet available at Maven Central, please build from source and use from you local maven repository):

```xml
...
    <dependency>
        <groupId>com.horcrux.components.searchandfilter</groupId>
        <artifactId>annotation</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.horcrux.components.searchandfilter</groupId>
        <artifactId>processor</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
...
```

## Documentation and getting help


## Building from Source

This Project uses Maven for its build. Java 8 is required for building the project from source. To build the complete project, run

    mvn clean install

from the root of the project directory. Now we can use as specified [here](#maven)
    
## Importing into IDE


### IntelliJ 

Make sure that you have at least IntelliJ 2018.2.x (needed since support for `annotationProcessors` from the `maven-compiler-plugin` is from that version).
Enable annotation processing in IntelliJ (Build, Execution, Deployment -> Compiler -> Annotation Processors)

### Eclipse

Make sure that you have the [m2e_apt](https://marketplace.eclipse.org/content/m2e-apt) plugin installed.

## Links

* [Homepage]()
* [Source code]()
* [Downloads]()
* [Issue tracker]()
* [User group]()
* [CI build]()

## Licensing
