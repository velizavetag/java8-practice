package edu.java8.practice.service.impl;

import edu.java8.practice.domain.Privilege;
import edu.java8.practice.domain.User;
import edu.java8.practice.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
//        Comparator<User> comparator = (u1, u2) -> u2.getAge() - u1.getAge();
//        comparator.thenComparing(u -> u.getFirstName());
//        Collections.sort(users, comparator);
        Collections.sort(users, Comparator.comparing(User::getFirstName));
        Collections.sort(users, Comparator.comparingInt(User::getAge).reversed());
        return users;
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        List<Privilege> list = users.stream()
                .flatMap(u -> u.getPrivileges().stream())
                .distinct()
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {

//        return users.stream()
//                .filter(u -> u.getAge()>age)
//                .filter(user -> user.getPrivileges().contains(Privilege.UPDATE))
//                .findAny();

        return users.stream()
                .filter(u -> u.getAge()>age && u.getPrivileges().contains(Privilege.UPDATE))
                .findAny();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(s -> s.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
//       DoubleSummaryStatistics statistics = users.stream()
//                .mapToDouble(u -> u.getAge())
//               .summaryStatistics();
//       return statistics.getAverage();

        return users.stream()
                .mapToDouble(u -> u.getAge())
                .average()
                .getAsDouble();
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
       Map<String, Long> map = users.stream()
               .collect(groupingBy(User::getLastName, counting()));
        String str = map.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(entry -> entry.getKey()).orElse("");
        long i = map.remove(str);

        if(map.containsValue(i)){
            return Optional.empty();
        }else{
            return Optional.of(str);
        }

    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {

        Predicate<User> allPredicates = x -> Arrays.stream(predicates)
                .mapToInt(predicate -> !predicate.test(x) ? 1 : 0).sum() == 0;

        return users.stream().filter(allPredicates).collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {

        return users.stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
//        return users.stream()
//                .collect(Collectors.groupingBy(user -> user.getPrivileges(). ));
       return Arrays.asList(Privilege.values())
               .stream()
               .map( p -> new AbstractMap.SimpleEntry(p , users.stream().filter(u -> u.getPrivileges().contains(p)).collect(Collectors.toList())))
               .collect(toMap(AbstractMap.SimpleEntry<Privilege, List<User>>::getKey, AbstractMap.SimpleEntry<Privilege, List<User>>::getValue));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.getLastName(), counting()));
    }
}
