package scc.srv;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;
import scc.db.HouseDBLayer;
import scc.db.UserDBLayer;

public class MainApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public MainApplication() {
		resources.add(ControlResource.class);
		resources.add(UserResource.class);
		resources.add(HouseResource.class);
		resources.add(RentalResource.class);
		resources.add(QuestionResource.class);
		resources.add(MediaResource.class);
		//TODO: Media Resource is singleton?
		singletons.add(UserDBLayer.getInstance());
		singletons.add(HouseDBLayer.getInstance());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
