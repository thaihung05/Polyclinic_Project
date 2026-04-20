import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders the homepage hero heading", () => {
  render(<App />);
  expect(
    screen.getByRole("heading", { name: /khám chữa bệnh miễn phí/i })
  ).toBeInTheDocument();
});
