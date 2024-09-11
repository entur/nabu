package no.rutebanken.nabu.security.permissionstore;

public record PermissionStorePermission(
  String operation,
  String responsibilityType,
  String responsibilityKey
) {}
