package com.raduvoinea.utils.generic;

import com.raduvoinea.utils.generic.dto.ITopologicalSortable;
import com.raduvoinea.utils.generic.exception.CircularDependencyException;

import java.util.*;

public class TopologicalSorter {

	public static <ID, Element extends ITopologicalSortable<ID>> void topologicalSort(List<Element> items) throws CircularDependencyException {
		Map<ID, Element> idToElement = new HashMap<>();
		for (Element item : items) {
			idToElement.put(item.getID(), item);
		}

		Map<ID, List<ID>> adjacencyList = new HashMap<>();
		for (Element item : items) {
			adjacencyList.computeIfAbsent(item.getID(), ignored -> new ArrayList<>());
		}

		for (Element item : items) {
			for (ID dependencyID : item.getDependencies()) {
				if (!idToElement.containsKey(dependencyID)) continue;
				adjacencyList.get(dependencyID).add(item.getID());
			}
		}

		List<ID> sortedIDs = internalTopologicalSort(adjacencyList);

		// In-place overwrite
		for (int i = 0; i < sortedIDs.size(); i++) {
			items.set(i, idToElement.get(sortedIDs.get(i)));
		}
	}

	private static <ID> List<ID> internalTopologicalSort(Map<ID, List<ID>> adjacencyList) throws CircularDependencyException {
		Map<ID, Integer> state = new HashMap<>();
		adjacencyList.forEach((node, neighbors) -> {
			state.putIfAbsent(node, 0);
			neighbors.forEach(neighbor -> state.putIfAbsent(neighbor, 0));
		});

		List<ID> sorted = new ArrayList<>();
		List<ID> path = new ArrayList<>();
		for (ID node : adjacencyList.keySet()) {
			if (state.get(node) == 0) {
				dfs(node, adjacencyList, state, sorted, path);
			}
		}

		Collections.reverse(sorted);
		return sorted;
	}

	private static <ID> void dfs(ID node, Map<ID, List<ID>> adjacencyList, Map<ID, Integer> state, List<ID> sorted, List<ID> path) throws CircularDependencyException {
		path.add(node);
		state.put(node, 1);

		try {
			for (ID neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
				if (state.get(neighbor) == 0) {
					dfs(neighbor, adjacencyList, state, sorted, path);
				} else if (state.get(neighbor) == 1) {
					int index = path.indexOf(neighbor);
					List<ID> cycle = new ArrayList<>(path.subList(index, path.size()));
					cycle.add(neighbor);
					throw new CircularDependencyException(cycle);
				}
			}
			state.put(node, 2);
			sorted.add(node);
		} finally {
			path.removeLast();
		}
	}

}