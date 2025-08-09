import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PolynomialSolver {
    public static void main(String[] args) throws Exception {
        // 1. Read JSON file
        String jsonStr = new String(Files.readAllBytes(Paths.get("data.json")));
        JSONObject obj = new JSONObject(jsonStr);

        int n = obj.getInt("n"); // number of roots provided
        int k = obj.getInt("k"); // minimum points needed (degree m => m+1 points)

        JSONArray points = obj.getJSONArray("points");

        // 2. Build matrix for Gaussian elimination: size k x (k+1)
        double[][] equations = new double[k][k + 1];

        for (int i = 0; i < k; i++) {
            JSONObject p = points.getJSONObject(i);
            int x = p.getInt("x");

            JSONObject yObj = p.getJSONObject("y");
            int base = yObj.getInt("base");
            String valueStr = yObj.getString("value");

            // Decode y from its base
            int yDecoded = Integer.parseInt(valueStr, base);

            // Fill coefficients for polynomial: a₀*x^(k-1) + a₁*x^(k-2) + ... + aₘ = y
            for (int j = 0; j < k; j++) {
                equations[i][j] = Math.pow(x, k - j - 1);
            }
            equations[i][k] = yDecoded;
        }

        // 3. Gaussian elimination
        for (int i = 0; i < k; i++) {
            // Make pivot = 1
            double pivot = equations[i][i];
            for (int j = 0; j <= k; j++) {
                equations[i][j] /= pivot;
            }

            // Eliminate other rows
            for (int r = 0; r < k; r++) {
                if (r != i) {
                    double factor = equations[r][i];
                    for (int c = 0; c <= k; c++) {
                        equations[r][c] -= factor * equations[i][c];
                    }
                }
            }
        }

        // 4. Coefficients are now in equations[i][k]
        double[] coeffs = new double[k];
        for (int i = 0; i < k; i++) {
            coeffs[i] = equations[i][k];
        }

        // The constant term "c" is always the last coefficient
        double c = coeffs[k - 1];
        System.out.println(c);
    }
}
