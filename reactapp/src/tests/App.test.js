import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import App from "../App";
import Home from "../components/Home";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import DocumentList from "../components/DocumentList";
import UploadPage from "../components/UploadPage";

jest.mock("axios");

describe("DigitalLockerAppTests", () => {
  test("rendersNavbarLinksCorrectly", () => {
    render(<Navbar />, { wrapper: MemoryRouter });
    expect(screen.getByText("Home")).toBeInTheDocument();
    expect(screen.getByText("Upload")).toBeInTheDocument();
    expect(screen.getByText("Documents")).toBeInTheDocument();
  });

  test("rendersFooterContent", () => {
    render(<Footer />);
    expect(screen.getByText(/© 2025 Digital Locker/i)).toBeInTheDocument();
  });

  test("rendersHomeContent", () => {
    render(<Home />, { wrapper: MemoryRouter });
    expect(screen.getByText("Digital Locker")).toBeInTheDocument();
    expect(screen.getByText("Manage your documents securely.")).toBeInTheDocument();
    expect(screen.getByText("Upload Document")).toBeInTheDocument();
  });
  test("fetchesAndDisplaysDocuments", async () => {
    axios.get.mockResolvedValueOnce({
      data: [{ name: "Doc1" }, { name: "Doc2" }]
    });

    render(<DocumentList />);
    await waitFor(() => {
      expect(screen.getByText("Doc1")).toBeInTheDocument();
      expect(screen.getByText("Doc2")).toBeInTheDocument();
    });
  });

  test("documentListTitleRenders", () => {
    render(<DocumentList />);
    expect(screen.getByText("Document List")).toBeInTheDocument();
  });

  test("uploadButtonIsClickable", () => {
    render(<UploadPage />);
    const button = screen.getByRole("button", { name: /upload/i });
    expect(button).toBeEnabled();
  });

  test("footerDisplaysCopyright", () => {
    render(<Footer />);
    expect(screen.getByText("© 2025 Digital Locker")).toBeInTheDocument();
  });

  test("navbarHasCorrectLinks", () => {
    render(<Navbar />, { wrapper: MemoryRouter });
    expect(screen.getByText("Home").getAttribute("href")).toBe("/");
    expect(screen.getByText("Upload").getAttribute("href")).toBe("/upload");
    expect(screen.getByText("Documents").getAttribute("href")).toBe("/documents");
  });


  test("documentListHandlesEmptyList", async () => {
    axios.get.mockResolvedValueOnce({ data: [] });
    render(<DocumentList />);
    await waitFor(() => {
      expect(screen.queryByRole("listitem")).not.toBeInTheDocument();
    });
  });

  test("rendersMultipleDocuments", async () => {
    axios.get.mockResolvedValueOnce({
      data: [{ name: "DocA" }, { name: "DocB" }]
    });
    render(<DocumentList />);
    expect(await screen.findByText("DocA")).toBeInTheDocument();
    expect(await screen.findByText("DocB")).toBeInTheDocument();
  });
});
