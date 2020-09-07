package com.mateusz.reader;

import com.mateusz.api.PlaceService;
import com.mateusz.api.VendorService;
import com.mateusz.enums.ReaderOption;
import com.mateusz.exception.CommandNotRecognizedException;
import com.mateusz.exception.NameInDatabaseAlreadyExistException;
import com.mateusz.exception.NameToRemoveNotExistInDatabaseException;
import com.mateusz.model.Place;
import com.mateusz.model.Vendor;
import com.mateusz.reader.parser.ReaderParser;
import com.mateusz.service.PlaceServiceImpl;
import com.mateusz.service.VendorServiceImpl;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionReader {
    private static ActionReader instance = null;
    private final VendorService vendorService = VendorServiceImpl.getInstance();
    private final PlaceService placeService = PlaceServiceImpl.getInstance();
    private final ReaderParser readerParser = ReaderParser.getInstance();

    private ActionReader() {
    }

    public static ActionReader getInstance() {
        if (instance == null) {
            instance = new ActionReader();
        }
        return instance;
    }

    public boolean initActionCommand(ArrayList<String> command, ReaderOption option) throws CommandNotRecognizedException, NameToRemoveNotExistInDatabaseException, NameInDatabaseAlreadyExistException {
        return actionCommand(command, option);
    }

    private boolean actionCommand(ArrayList<String> command, ReaderOption option) throws CommandNotRecognizedException, NameToRemoveNotExistInDatabaseException, NameInDatabaseAlreadyExistException {
        StringBuilder address = new StringBuilder();
        String city = null;
        String name = null;
        String utility = null;
        String postcode = null;

        String parameter = command.get(0);
        command.remove(0);
        Iterator<String> iter = command.iterator();

        while (iter.hasNext()) {
            String s = iter.next();
            switch (s) {
                case "-address":
                case "-a":
                    address.append(iter.next());
                    address.append(" ");
                    address.append(iter.next());
                    break;
                case "-city":
                case "-c":
                    city = iter.next();
                    break;
                case "-name":
                case "-n":
                    name = iter.next();
                    break;
                case "-utility":
                case "-u":
                    utility = iter.next();
                    break;
                case "-postcode":
                case "-p":
                    postcode = iter.next();
                    break;
                default:
                    throw new CommandNotRecognizedException("Parameter '" + s + "' not recognized! Type 'help' command to more information.");
            }
        }

        if (parameter.equals("vendor")) {
            Vendor vendor = new Vendor(name, utility);
            return runVendorOption(vendor, option);
        } else if (parameter.equals("place")) {
            Place place = new Place(name, null, null, postcode, city);
            if (option.equals(ReaderOption.ADD)) {
                place = readerParser.splitAddress(place, address.toString());
            }
            return runPlaceOption(place, option);
        }
        return false;
    }

    private boolean runVendorOption(Vendor vendor, ReaderOption option) throws NameToRemoveNotExistInDatabaseException, NameInDatabaseAlreadyExistException {
            switch (option) {
                case ADD:
                    vendorService.addVendor(vendor);
                    return true;
                case REMOVE:
                    vendorService.removeVendorByName(vendor.getName());
                    return true;
                case SHOW:
                    //TODO: return via toString()
                    for (Vendor v : vendorService.getAllVendors()) {
                        System.out.println(v.getName() + " (" + v.getUtility() + ")");
                    }
                    return true;
            }
            return false;
    }

    private boolean runPlaceOption(Place place, ReaderOption option) throws NameToRemoveNotExistInDatabaseException, NameInDatabaseAlreadyExistException {
        switch (option) {
            case ADD:
                placeService.addPlace(place);
                return true;
            case REMOVE:
                placeService.removePlaceByName(place.getName());
                return true;
            case SHOW:
                //TODO: return via toString()
                for (Place p : placeService.getAllPlaces()) {
                    System.out.println(p.getName() + " (" + p.getStreet() + ")");
                }
                return true;
        }
        return false;
    }
}
