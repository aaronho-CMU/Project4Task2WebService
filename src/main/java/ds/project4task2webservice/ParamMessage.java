package ds.project4task2webservice;

public class ParamMessage {
    String start;
    String end;
    String transport_mode;
    String vehicle_make;
    String vehicle_model;
    String vehicle_year;
    String vehicle_engine_size;
    String vehicle_fuel_type;
    String vehicle_submodel;
    String vehicle_transmission_type;
    String num_passengers;

    public ParamMessage(String start, String end, String transport_mode, String vehicle_make, String vehicle_model, String vehicle_year, String vehicle_engine_size, String vehicle_fuel_type, String vehicle_submodel, String vehicle_transmission_type, String num_passengers) {
        this.start = start;
        this.end = end;
        this.transport_mode = transport_mode;
        this.vehicle_make = vehicle_make;
        this.vehicle_model = vehicle_model;
        this.vehicle_year = vehicle_year;
        this.vehicle_engine_size = vehicle_engine_size;
        this.vehicle_fuel_type = vehicle_fuel_type;
        this.vehicle_submodel = vehicle_submodel;
        this.vehicle_transmission_type = vehicle_transmission_type;
        this.num_passengers = num_passengers;
    }
}
