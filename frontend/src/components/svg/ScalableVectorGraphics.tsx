"use client"

import SVG_CATALOG from "./svg_catalog";
import Image from "next/image";

/**
 * SVG Component - Renders SVG icons from the catalog
 *
 * @param name - The icon name from the catalog
 * @param className - Tailwind classes for styling
 * @param width - Width in pixels (default: 24)
 * @param height - Height in pixels (default: 24)
 *
 * @param props
 * @example
 * <SVG name="menu-dashboard" className="w-6 h-6" />
 */
export const SVG = ({
  name,
  className = "",
  width = 24,
  height = 24,
  ...props
}: {
  name: keyof typeof SVG_CATALOG;
  className?: string;
  width?: number;
  height?: number;
}) => {
  const svgPath = SVG_CATALOG[name];

  if (!svgPath) {
    console.warn(`SVG icon "${name}" not found in catalog`);
    return null;
  }

  return (
    <Image
      src={svgPath}
      alt={name}
      width={width}
      height={height}
      className={className}
      {...props}
    />
  );
};
