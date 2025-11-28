# DetroitChow Admin API - HTTP Test Files

This directory contains HTTP request files for manually testing the DetroitChow Admin API endpoints.

## Quick Start

### IntelliJ IDEA (Built-in)
1. Open any `.http` file
2. Click the green ▶️ play button next to any request
3. Switch environments using the dropdown at the top (dev/prod)

### VS Code
1. Install the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension
2. Open any `.http` file
3. Click "Send Request" above any request
4. Switch environments in VS Code settings

## Files

- **`http-client.env.json`** - Environment configuration (dev/prod)
- **`locations.http`** - Location endpoints (GET, POST, PUT, DELETE)
- **`menus.http`** - Menu endpoints (GET, POST, PUT, DELETE, Reorder)

## Environment Variables

The `http-client.env.json` file defines two environments:

**dev** (default)
- Base URL: `http://localhost:8080/api/v1`
- For local development

**prod**
- Base URL: `https://api.detroitchow.com/api/v1`
- For production testing

## Usage Examples

### Testing Location CRUD

1. **Get all locations:**
   - Open `locations.http`
   - Run the "Get all active and temporarily closed locations" request

2. **Create a new location:**
   - Run one of the POST requests
   - Note the returned `locationid` from the response

3. **Update the location:**
   - Copy the `locationid` from step 2
   - Update the PUT request with that ID
   - Modify fields as needed
   - Run the request

4. **Delete the location:**
   - Copy the `locationid`
   - Update the DELETE request
   - Run the request

### Testing Menus

1. **Add menus to a location:**
   - Use a valid `locationid` (like "32")
   - Run the POST requests to add menus
   - Note the returned `menuId` values

2. **Reorder menus:**
   - Copy the menu IDs from step 1
   - Update the reorder request with those IDs
   - Change the order in the array
   - Run the request

3. **Update/Delete menus:**
   - Use the menu IDs from step 1
   - Run PUT or DELETE requests

## Request Organization

Each file is organized into sections:

```
###############################################################################
# SECTION NAME
###############################################################################

### Description of specific request
HTTP_METHOD {{baseUrl}}/path
Headers
Body (if applicable)
```

The `###` separator allows you to run individual requests.

## Tips

1. **Variables:** Use `{{baseUrl}}` instead of hardcoding URLs
2. **Comments:** Lines starting with `#` or `###` are comments
3. **Sequential Requests:** Use `###` to separate requests that should run in sequence
4. **Response Inspection:** Results appear in a separate window/panel
5. **Save Responses:** You can save response bodies for later comparison

## Common HTTP Status Codes

- **200 OK** - Successful GET/PUT/DELETE
- **201 Created** - Successful POST
- **400 Bad Request** - Invalid input data
- **404 Not Found** - Resource doesn't exist
- **500 Internal Server Error** - Server error

## Testing Workflow

1. Start the Spring Boot application: `mvn spring-boot:run`
2. Open an `.http` file
3. Run requests in order (top to bottom typically works)
4. Verify responses match expected behavior
5. Test error cases at the end of each file

## Notes

- Replace placeholder IDs (like `32`, `test-location-id`) with actual values from your database
- The "Error Cases" sections demonstrate validation and error handling
- Some requests depend on previous requests (e.g., you need to create before you can update)
- Menu reordering requires existing menus with valid IDs

## Further Reading

- [IntelliJ HTTP Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)
- [VS Code REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)
- [HTTP Request Syntax](https://www.jetbrains.com/help/idea/exploring-http-syntax.html)
