package keville.model.event.location;

public class Location {

  //country, region, locality tries to emulate ISO-3166-2
  //https://en.wikipedia.org/wiki/ISO_3166-2
  public String name;
  public String country;
  public String region;
  public String locality;
  public String streetAddress;
  public Double latitude;
  public Double longitude;

  public Location(String name,
                  String country,
                  String region,
                  String locality,
                  String streetAddress,
                  Double latitude,
                  Double longitude) {
    this.name = name;
    this.country = country;
    this.region = region;
    this.locality = locality;
    this.streetAddress = streetAddress;
    this.latitude = latitude;
    this.longitude = longitude;    
  }

  public String toString() {

    String result = "";
    if ( name != null ) {
      result="name: " +  this.name;
    }

    if ( latitude != null &&  longitude != null ) {
      result += "\nlatitude : " + this.latitude + "\t longitude " + this.longitude;
    }

    if ( country != null ) {
      result+="\ncountry: " +  this.country;
    }

    if ( region != null ) {
      result+="\nregion: " +  this.region;
    }

    if ( locality != null ) {
      result+="\nlocality: " +  this.locality;
    }

    return result;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    public String name;
    public String country;
    public String region;
    public String locality;
    public String streetAddress;
    public Double latitude;
    public Double longitude;


    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setCountry(String country) {
      this.country = country;
      return this;
    }

    public Builder setRegion(String region) {
      this.region = region;
      return this;
    }

    public Builder setLocality(String locality) {
      this.locality = locality;
      return this;
    }

    public Builder streetAddress(String streetAddress) {
      this.streetAddress = streetAddress;
      return this;
    }

    public Builder setLatitude(Double latitude) {
      this.latitude = latitude;
      return this;
    }

    public Builder setLongitude(Double longitude) {
      this.longitude = longitude;
      return this;
    }

    public Location build() {

      Location location = 
        new Location(
            name,
            country,
            region,
            locality,
            streetAddress,
            latitude,
            longitude
        );

      return location;
    }

  }

}
