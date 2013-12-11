package org.adiusframework.resource;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfigurableConverterManager implements ConverterManager {

	private ConcurrentMap<Class<? extends Resource>, List<Converter<?, ?>>> from;

	private ConcurrentMap<Class<? extends Resource>, List<Converter<?, ?>>> to;

	public ConfigurableConverterManager() {
		from = new ConcurrentHashMap<Class<? extends Resource>, List<Converter<?, ?>>>();
		to = new ConcurrentHashMap<Class<? extends Resource>, List<Converter<?, ?>>>();
	}

	public void setConverters(List<Converter<?, ?>> converters) {
		for (Converter<?, ?> converter : converters) {
			List<Converter<?, ?>> listFrom = from.get(converter.getClassFrom());
			if (listFrom == null) {
				listFrom = new Vector<Converter<?, ?>>();
				from.put(converter.getClassFrom(), listFrom);
			}
			listFrom.add(converter);
			List<Converter<?, ?>> listTo = to.get(converter.getClassTo());
			if (listTo == null) {
				listTo = new Vector<Converter<?, ?>>();
				to.put(converter.getClassTo(), listTo);
			}
			listTo.add(converter);
		}
	}

	@Override
	public List<Converter<? extends Resource, ? extends Resource>> getConvertersFrom(Class<? extends Resource> from,
			boolean noneTransientOnly) {
		return getFilteredConverters(from, this.from, noneTransientOnly);
	}

	@Override
	public List<Converter<? extends Resource, ? extends Resource>> getConvertersTo(Class<? extends Resource> to,
			boolean noneTransientOnly) {
		return getFilteredConverters(to, this.to, noneTransientOnly);
	}

	@Override
	public List<Converter<? extends Resource, ? extends Resource>> getConverters(Class<? extends Resource> from,
			Class<? extends Resource> to, boolean noneTransientOnly) {
		List<Converter<?, ?>> candidates = getFilteredConverters(from, this.from, noneTransientOnly);
		List<Converter<?, ?>> list = new Vector<Converter<?, ?>>();
		for (Converter<?, ?> candidate : candidates) {
			if (candidate.getClassTo().equals(to))
				list.add(candidate);
		}
		return list;
	}

	protected List<Converter<? extends Resource, ? extends Resource>> getFilteredConverters(
			Class<? extends Resource> filter, ConcurrentMap<Class<? extends Resource>, List<Converter<?, ?>>> map,
			boolean noneTransientOnly) {

		// list to store the found converters
		List<Converter<?, ?>> list = new Vector<Converter<?, ?>>();

		// lets find some candidates, if none is found we have to return an
		// empty list
		List<Converter<?, ?>> candidates = map.get(filter);
		if (candidates == null)
			return list;

		// if none transient only there is nothing more to do
		if (!noneTransientOnly)
			return candidates;

		// otherwise we have to clear the list
		for (Converter<?, ?> candidate : candidates) {
			if (!candidate.getClass().isAnnotationPresent(Transient.class))
				list.add(candidate);
		}
		return list;
	}

}
